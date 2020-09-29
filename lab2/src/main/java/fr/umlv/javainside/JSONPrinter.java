package fr.umlv.javainside;

import fr.umlv.javainside.annotation.JSONProperty;

import java.io.UncheckedIOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.RecordComponent;
import java.lang.reflect.UndeclaredThrowableException;
import java.util.Arrays;
import java.util.stream.Collectors;

public class JSONPrinter {
    public static String toJSON(Record record) {
        return "{"+ Arrays.stream(record.getClass().getRecordComponents())
                .map(recordComponent -> '\"' +
                        recordComponentName(recordComponent) +
                        "\": " +
                        addQuoteIfIsStringTypeComponent(recordComponent) +
                        invokeAccessor(recordComponent.getAccessor(), record) +
                        addQuoteIfIsStringTypeComponent(recordComponent)
                ).collect(Collectors.joining(", "))
                + " }";
    }

    private static char addQuoteIfIsStringTypeComponent(RecordComponent recordComponent) {
        if (recordComponent.getType().equals(String.class)) {
            return '\"';
        }
        return '\0';
    }

    private static Object invokeAccessor(Method accessor, Record record) {
        try {
            return accessor.invoke(record);
        } catch (IllegalAccessException e) {
            throw (IllegalAccessError) new IllegalAccessError().initCause(e);
        } catch (InvocationTargetException e) {
            Throwable cause = e.getCause();
            if (cause instanceof RuntimeException runtimeException)
                throw runtimeException;
            if (cause instanceof Error error)
                throw new RuntimeException(error);
            throw new UndeclaredThrowableException(cause);
        }
    }

    private static String recordComponentName(RecordComponent recordComponent) {
        JSONProperty jsonPropertyAnnotation = recordComponent.getAnnotation(JSONProperty.class);

        if (jsonPropertyAnnotation == null)
            return recordComponent.getName();
        return jsonPropertyAnnotation.value();
    }
}
