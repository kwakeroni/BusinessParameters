package be.kwakeroni.parameters.backend.inmemory.api.type;

import java.util.Comparator;
import java.util.function.Function;

/**
 * (C) 2016 Maarten Van Puymbroeck
 */
public interface ComparableParameterType<T> extends ParameterType<T> {

    public int compare(T one, T two);

    public static <T> ComparableParameterType<T> of(Function<? super T, String> serializer, Function<String, ? extends T> deserializer, Comparator<? super T> comparator){
        return new ComparableParameterType<T>() {
            @Override
            public String convertToString(T value) {
                return serializer.apply(value);
            }

            @Override
            public T convertFromString(String value) {
                return deserializer.apply(value);
            }

            @Override
            public int compare(T one, T two) {
                return comparator.compare(one, two);
            }
        };
    }

    public static <T extends Comparable<T>> ComparableParameterType<T> of(Function<? super T, String> serializer, Function<String, ? extends T> deserializer){
        return new ComparableParameterType<T>() {
            @Override
            public String convertToString(T value) {
                return serializer.apply(value);
            }

            @Override
            public T convertFromString(String value) {
                return deserializer.apply(value);
            }

            @Override
            public int compare(T one, T two) {
                return one.compareTo(two);
            }
        };
    }


}
