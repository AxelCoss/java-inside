package fr.umlv.javainside;

import fr.umlv.javainside.annotation.JSONProperty;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.RecordComponent;
import java.lang.reflect.UndeclaredThrowableException;
import java.util.Arrays;
import java.util.stream.Collectors;

public class JSONPrinter {


    private static final ClassValue<RecordComponent[]> recordComponentCache = new ClassValue<RecordComponent[]>() {
        @Override
        protected RecordComponent[] computeValue(Class type) {
            return type.getRecordComponents();
        }
    };

    public static String toJSON(Record record) {
        return "{"+ Arrays.stream(getRecordComponentsUsingCache(record))
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
        if (jsonPropertyAnnotation.value().equals(""))
            return recordComponent.getName().replace('_', '-');
        return jsonPropertyAnnotation.value();
    }

    private static RecordComponent[] getRecordComponentsUsingCache(Record record) {
        return recordComponentCache.get(record.getClass());
    }
}
