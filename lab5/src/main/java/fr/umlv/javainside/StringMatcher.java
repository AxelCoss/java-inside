package fr.umlv.javainside;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MutableCallSite;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.lang.invoke.MethodHandles.*;
import static java.lang.invoke.MethodType.methodType;

public class StringMatcher {
    private static final MethodHandle EQUALS;

    static {
        try {
            EQUALS = publicLookup().findVirtual(String.class, "equals", methodType(boolean.class, Object.class));
        } catch (NoSuchMethodException | IllegalAccessException e) {
            throw new AssertionError(e);
        }
    }

    public static MethodHandle matchWithGWTs(Map<String, Integer> mapping) {
        var mh = dropArguments(constant(int.class, -1), 0, String.class);
        for (var entry : mapping.entrySet()) {
            var text = entry.getKey();
            var index = entry.getValue();
            var test = insertArguments(EQUALS, 1, text);
            var target = dropArguments(constant(int.class, index), 0, String.class);
            mh = guardWithTest(test, target, mh);
        }
        return mh;
    }

    public static MethodHandle matchWithAnInliningCache(Map<String, Integer> mapping) {
        return new InliningCache(mapping).dynamicInvoker();
    }

    private static class InliningCache extends MutableCallSite {
        private static final MethodHandle SLOW_PATH;
        static {
            var lookup = lookup();
            try {
                SLOW_PATH = lookup.findVirtual(InliningCache.class, "slowPath", methodType(int.class, String.class));
            } catch (NoSuchMethodException | IllegalAccessException e) {
                throw new AssertionError(e);
            }
        }

        private final Map<String, Integer> mapping;

        public InliningCache(Map<String, Integer> mapping) {
            super(methodType(int.class, String.class));
            this.mapping = mapping;
            setTarget(MethodHandles.insertArguments(SLOW_PATH, 0, this));
        }

        private int slowPath(String text) {
            var index = mapping.getOrDefault(text, -1);

            var test = insertArguments(EQUALS, 1, text);
            var target = dropArguments(constant(int.class, index), 0, String.class);

            var guard = guardWithTest(test, target, new InliningCache(mapping).dynamicInvoker());

            setTarget(guard);
            return index;
        }
    }

    private static final MethodHandle HASH_CODE;
    static {
        var lookup = lookup();
        try {
            HASH_CODE = lookup().findVirtual(String.class, "hashCode", methodType(int.class));
        } catch (NoSuchMethodException | IllegalAccessException e) {
            throw new AssertionError(e);
        }
    }

    public static MethodHandle matchUsingHashCodes(Map<String, Integer> mapping) {
        var stringsGroupedByHash = mapping.keySet().stream().collect(Collectors.groupingBy(String::hashCode));

        int[] hashes = new int[stringsGroupedByHash.size()];
        MethodHandle[] mhs = new MethodHandle[stringsGroupedByHash.size() + 1];

        var index = 0;
        for (var entry : stringsGroupedByHash.entrySet()) {
            var hash = entry.getKey();
            var strings = entry.getValue();

            hashes[index] = hash;
            mhs[index] = dropArguments(
                    matchWithGWTs(
                                    strings.stream().collect(Collectors.toMap(Function.identity(), mapping::get))
                            )
                        , 0, int.class);
            index++;
        }

        mhs[mhs.length - 1] = dropArguments(constant(int.class, -1), 0, int.class, String.class);

//        int[] hashes = stringsGroupedByHash.keySet()
//                .stream()
//                .mapToInt(Integer::intValue)
//                .toArray();
//
//        MethodHandle[] mhs = Stream.concat(
//                stringsGroupedByHash.keySet()
//                    .stream()
//                    .map(value ->
//                        dropArguments(
//                            matchWithGWTs(
//                                mapping.entrySet()
//                                        .stream()
//                                        .filter(entry -> entry.getKey().hashCode() == value)
//                                        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue))
//                            )
//                        , 0, int.class)
//                ),
//                Stream.of(dropArguments(constant(int.class, -1), 0, int.class, String.class))
//        ).toArray(MethodHandle[]::new);

        return foldArguments(LookupSwitchGenerator.lookupSwitch(hashes, mhs), HASH_CODE);
    }


}
