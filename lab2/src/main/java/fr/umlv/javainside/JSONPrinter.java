package fr.umlv.javainside;

import fr.umlv.javainside.annotation.JSONProperty;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.RecordComponent;
import java.lang.reflect.UndeclaredThrowableException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class JSONPrinter {

    private static final ClassValue<List<Function<Record, String>>> cache = new ClassValue<List<Function<Record, String>>>() {
        @Override
        protected List<Function<Record, String>> computeValue(Class<?> type) {
            return Arrays.stream(type.getRecordComponents())
                    .<Function<Record, String>>map(recordComponent -> (Record record) ->
                            '\"' +
                            recordComponentName(recordComponent) +
                            "\": " +
                            escape(invokeAccessor(recordComponent.getAccessor(), record))
                    ).collect(Collectors.toList());
        }
    };

    public static String toJSON(Record record) {
        return cache.get(record.getClass())
                .stream()
                .map(function -> function.apply(record))
                .collect(Collectors.joining(", ", "{", "}"));

    }

    private static String escape(Object o) {
        return o instanceof String ? "\"" + o + "\"" : "" + o;
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
        if (jsonPropertyAnnotation.value().equals(""))
            return recordComponent.getName().replace('_', '-');
        return jsonPropertyAnnotation.value();
    }
}
