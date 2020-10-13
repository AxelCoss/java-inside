package fr.umlv.javainside;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import static java.lang.invoke.MethodType.methodType;
import static java.util.Objects.requireNonNull;

import java.lang.reflect.UndeclaredThrowableException;
import java.util.Objects;
import java.util.function.Consumer;

public interface Logger {
    public void log(String message);

    public static Logger of(Class<?> declaringClass, Consumer<? super String> consumer) {
        var mh = createLoggingMethodHandle(declaringClass, consumer);
        return new Logger() {
            @Override
            public void log(String message) {
                try {
                    mh.invokeExact(message);
                } catch(RuntimeException | Error e) {
                    throw e;
                } catch(Throwable t) {
                    throw new UndeclaredThrowableException(t);
                }
            }
        };
    }

    class Implementation {
        private static final MethodHandle ACCEPT;

        static {
            var lookup = MethodHandles.lookup();
            try {
                ACCEPT = lookup.findVirtual(Consumer.class, "accept", methodType(void.class, Object.class));
            } catch (NoSuchMethodException | IllegalAccessException e) {
                throw new AssertionError(e);
            }
        }
    }

    private static MethodHandle createLoggingMethodHandle(Class<?> declaringClass, Consumer<? super String> consumer) {
        requireNonNull(consumer);
        requireNonNull(declaringClass);
        var methodHandle = Implementation.ACCEPT.bindTo(consumer);
        methodHandle = methodHandle.asType(methodType(void.class, String.class));
        return methodHandle;
    }
}