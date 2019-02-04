package be.kwakeroni.scratch.test;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.function.Function;
import java.util.function.IntFunction;

public class ExtArrays {

    private ExtArrays() {
        throw new UnsupportedOperationException("Cannot instantiate utility class");
    }

    private static <T> T[] newArray(T[] example, int length) {
        Class<?> cType = example.getClass().getComponentType();
        return (T[]) Array.newInstance(cType, length);
    }

    public static <T, S> Object[] map(T[] array, Function<? super T, ? extends S> mapper) {
        return map(array, mapper, Object[]::new);
    }

    public static <T, S> S[] map(T[] array, Function<? super T, ? extends S> mapper, IntFunction<S[]> supplier) {
        return Arrays.stream(array).map(mapper).toArray(supplier);
    }

    public static <T> T[] select(T[] array, int startIndex, int endIndexExclusive) {
        T[] newArray = newArray(array, endIndexExclusive - startIndex);
        System.arraycopy(array, startIndex, newArray, 0, newArray.length);
        return newArray;
    }

    public static <T> T[] prepend(T value, T[] array) {
        T[] newArray = newArray(array, array.length + 1);
        newArray[0] = value;
        System.arraycopy(array, 0, newArray, 1, array.length);
        return newArray;
    }

    public static <T> T[] prepend(T value0, T value1, T[] array) {
        T[] newArray = newArray(array, array.length + 2);
        newArray[0] = value0;
        newArray[1] = value1;
        System.arraycopy(array, 0, newArray, 2, array.length);
        return newArray;
    }

    public static <T> T[] replace(T[] array, int index, T value) {
        T[] copy = newArray(array, array.length);
        System.arraycopy(array, 0, copy, 0, array.length);
        copy[index] = value;
        return copy;
    }
}
