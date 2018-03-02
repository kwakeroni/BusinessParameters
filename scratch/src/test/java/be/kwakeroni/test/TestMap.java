package be.kwakeroni.test;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

public final class TestMap {

    private TestMap() {

    }

    public static Map<String, String> of(String key, String value, String... andSoOn) {
        return of(key, value, (Object[]) andSoOn);
    }

    public static <K, V> Map<K, V> of(K key, V value, Object... andSoOn) {
        Map<K, V> map = new HashMap<>(andSoOn.length / 2 + 1);

        forEach(map::put, key, value, andSoOn);

        return Collections.unmodifiableMap(map);
    }

    public static void forEach(BiConsumer<String, String> action, String key, String value, String... andSoOn) {
        forEach(action, key, value, (Object[]) andSoOn);
    }

    public static <K, V> void forEach(BiConsumer<K, V> action, K key, V value, Object... andSoOn) {
        if (andSoOn.length % 2 != 0) {
            throw new IllegalArgumentException("Expected parameter-value pairs: " + Arrays.toString(andSoOn));
        }

        action.accept(key, value);
        for (int i = 0; i < andSoOn.length; i += 2) {
            action.accept((K) andSoOn[i], (V) andSoOn[i + 1]);
        }
    }

}
