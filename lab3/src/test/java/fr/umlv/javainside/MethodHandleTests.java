package fr.umlv.javainside;

import org.junit.jupiter.api.Test;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.WrongMethodTypeException;
import java.util.List;

import static java.lang.invoke.MethodHandles.*;
import static java.lang.invoke.MethodType.methodType;
import static org.junit.jupiter.api.Assertions.*;

public class MethodHandleTests {

    @Test
    public void  findStaticTest() throws NoSuchMethodException, IllegalAccessException {
        var lookup = MethodHandles.lookup();
        var methodType = methodType(int.class, String.class);
        var methodHandle = lookup.findStatic(Integer.class, "parseInt", methodType);

        assertEquals(methodType(int.class, String.class), methodHandle.type());
    }


    @Test
    public void findVirtualTest() throws NoSuchMethodException, IllegalAccessException {
        var lookup = MethodHandles.lookup();
        var methodType = methodType(String.class);
        var methodHandle = lookup.findVirtual(String.class, "toUpperCase", methodType);

        assertEquals(methodType(String.class, String.class), methodHandle.type());
    }


    @Test
    public void invokeExactStaticTest() throws Throwable {
        var lookup = MethodHandles.lookup();
        var methodType = methodType(int.class, String.class);
        var methodHandle = lookup.findStatic(Integer.class, "parseInt", methodType);
        var result = (int) methodHandle.invokeExact("444");

        assertEquals(444, result);
    }


    @Test
    public void  invokeExactStaticWrongArgumentTest() throws Throwable {
        var lookup = MethodHandles.lookup();
        var methodType = methodType(int.class, String.class);
        var methodHandle = lookup.findStatic(Integer.class, "parseInt", methodType);

        assertThrows(WrongMethodTypeException.class, () -> {var result = (int) methodHandle.invokeExact();});
    }

    @Test
    public void invokeExactVirtualTest() throws Throwable {
        var lookup = MethodHandles.lookup();
        var methodType = methodType(String.class);
        var methodHandle = lookup.findVirtual(String.class, "toUpperCase", methodType);
        var result = (String) methodHandle.invokeExact("abcd");
        assertEquals("ABCD", result);
    }


    @Test
    public void invokeExactVirtualWrongArgumentTest() throws NoSuchMethodException, IllegalAccessException {
        var lookup = MethodHandles.lookup();
        var methodType = methodType(String.class);
        var methodHandle = lookup.findVirtual(String.class, "toUpperCase", methodType);

        assertThrows(WrongMethodTypeException.class, () -> {
            var result = (String)methodHandle.invokeExact(1);
        });
        assertThrows(WrongMethodTypeException.class, () -> {
            var result = (String)methodHandle.invokeExact(true);
        });
        assertThrows(WrongMethodTypeException.class, () -> {
            var result = (String)methodHandle.invokeExact();
        });
    }

    @Test
    public void invokeStaticTest() throws NoSuchMethodException, IllegalAccessException {
        var lookup = MethodHandles.lookup();
        var methodType = methodType(int.class, String.class);
        var methodHandle = lookup.findStatic(Integer.class, "parseInt", methodType);

        assertAll(
                () -> assertEquals(1, (Integer) methodHandle.invoke("1")),
                () -> assertThrows(WrongMethodTypeException.class, () -> {
                    var result = (String) methodHandle.invoke("1");
                })
        );
    }

    @Test
    public void invokeVirtualTest() throws NoSuchMethodException, IllegalAccessException {
        var lookup = MethodHandles.lookup();
        var methodType = methodType(String.class);
        var methodHandle = lookup.findVirtual(String.class, "toUpperCase", methodType);

        assertAll(
                () -> assertEquals("ABCD", (Object) methodHandle.invoke("abcd")),
                () -> assertThrows(WrongMethodTypeException.class, () -> {
                    var result = (double) methodHandle.invoke("abcd");
                })
        );
    }

    @Test
    public void insertAndInvokeStaticTest() throws NoSuchMethodException, IllegalAccessException {
        var lookup = MethodHandles.lookup();
        var methodType = methodType(int.class, String.class);
        var methodHandle = lookup.findStatic(Integer.class, "parseInt", methodType);
        var methodHandleWithArgument = MethodHandles.insertArguments(methodHandle, 0, "123");

        assertAll(
                () -> assertEquals(123, (Integer) methodHandleWithArgument.invoke()),
                () -> assertThrows(WrongMethodTypeException.class, () -> {
                    var result = (String) methodHandleWithArgument.invoke();
                })
        );
    }

    @Test
    public void bindToAndInvokeVirtualTest() throws NoSuchMethodException, IllegalAccessException {
        var lookup = MethodHandles.lookup();
        var methodType = methodType(String.class);
        var methodHandle = lookup.findVirtual(String.class, "toUpperCase", methodType);
        var methodHandleWithArgument = methodHandle.bindTo("abcd");

        assertAll(
                () -> assertEquals("ABCD", (String) methodHandleWithArgument.invoke()),
                () -> assertThrows(WrongMethodTypeException.class, () -> {
                    var result = (double) methodHandleWithArgument.invoke();
                })
        );
    }

    @Test
    public void dropAndInvokeStaticTest() throws Throwable {
        var lookup = MethodHandles.lookup();
        var methodType = methodType(int.class, String.class);
        var methodHandle = lookup.findStatic(Integer.class, "parseInt", methodType);
        var methodHandleWithOneDroppedArgument = dropArguments(methodHandle, 1, String.class);

        assertEquals(123, (Integer) methodHandleWithOneDroppedArgument.invoke("123", "1"));
    }

    @Test
    public void dropAndInvokeVirtualTest() throws Throwable {
        var lookup = MethodHandles.lookup();
        var methodType = methodType(String.class);
        var methodHandle = lookup.findVirtual(String.class, "toUpperCase", methodType);
        var methodHandleWithOneDroppedArgument = dropArguments(methodHandle, 1, String.class);

        assertEquals("ABCD", (String) methodHandleWithOneDroppedArgument.invoke("abcd", "Test1"));
    }


    @Test
    public void asTypeAndInvokeExactStaticTest() throws NoSuchMethodException, IllegalAccessException {
        var lookup = MethodHandles.lookup();
        var methodType = methodType(int.class, String.class);
        var methodHandle = lookup.findStatic(Integer.class, "parseInt", methodType);
        var methodWithNewType = methodHandle.asType(methodType(Integer.class, String.class));

        assertAll(
                () -> assertEquals(1, (Integer) methodWithNewType.invokeExact("1")),
                () -> assertThrows(WrongMethodTypeException.class, () -> {
                    var result = (String) methodWithNewType.invokeExact("1");
                })
        );
    }


    @Test
    public void invokeExactConstantTest() throws Throwable {
        var methodHandle = constant(int.class, 42);

        assertEquals(42, (int) methodHandle.invokeExact());
    }


    private static MethodHandle match(String text) throws NoSuchMethodException, IllegalAccessException {
        var test = MethodHandles
                .lookup()
                .findVirtual(String.class, "equals", methodType(boolean.class, Object.class));
        var testWithArgument = MethodHandles.insertArguments(test, 1, text);
        var target = dropArguments(constant(int.class, 1), 0, String.class);
        var fallback = dropArguments(constant(int.class, -1), 0, String.class);

        return MethodHandles.guardWithTest(testWithArgument, target, fallback);
    }

    @Test
    public void matchTest() throws Throwable {
        var methodHandle = match("abcD");
        assertAll(
                () -> assertEquals(1, (int) methodHandle.invokeExact("abcD")),
        () -> assertEquals(-1, (int) methodHandle.invokeExact("Not abcD"))
        );
    }

    private static MethodHandle matchAll(List<String> listOfText) throws NoSuchMethodException, IllegalAccessException {
        var equals = MethodHandles
            .lookup()
            .findVirtual(String.class, "equals", methodType(boolean.class, Object.class));
        var fallback = dropArguments(constant(int.class, -1), 0, String.class);
        int index = 0;
        for (var text: listOfText) {
            var test = MethodHandles.insertArguments(equals, 1, text);
            var target = dropArguments(constant(int.class, index), 0, String.class);
            fallback = guardWithTest(test, target, fallback);
            index++;
        }
        return fallback;
    }

    @Test
    public void matchAllTest() throws Throwable {
        var methodHandle = matchAll(List.of("0", "1", "2", "3", "4"));
        assertAll(
                () -> assertEquals(0, (int) methodHandle.invokeExact("0")),
                () -> assertEquals(1, (int) methodHandle.invokeExact("1")),
                () -> assertEquals(2, (int) methodHandle.invokeExact("2")),
                () -> assertEquals(3, (int) methodHandle.invokeExact("3")),
                () -> assertEquals(4, (int) methodHandle.invokeExact("4")),
                () -> assertEquals(-1, (int) methodHandle.invokeExact("5"))
        );
    }


}

