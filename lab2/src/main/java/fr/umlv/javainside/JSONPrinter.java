package fr.umlv.javainside;

import java.lang.reflect.RecordComponent;
import java.util.Arrays;
import java.util.stream.Collectors;

public class JSONPrinter {
    public static String toJSON(Record record) {
        return Arrays.stream(record.getClass().getRecordComponents())
                .map(RecordComponent::getName)
                .collect(Collectors.joining(", "));
    }
}
