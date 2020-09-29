package fr.umlv.javainside;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.stream.Collectors;

public class JSONPrinter {
    public static String toJSON(Record record) {
        return Arrays.stream(record.getClass().getRecordComponents())
                .map(recordComponent -> {
                    try {
                        return recordComponent.getName() +
                                ' ' +
                                recordComponent.getAccessor().invoke(record);
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        System.err.println(e.getMessage());
                        return "";
                    }
                })
                .collect(Collectors.joining(", "));
    }
}
