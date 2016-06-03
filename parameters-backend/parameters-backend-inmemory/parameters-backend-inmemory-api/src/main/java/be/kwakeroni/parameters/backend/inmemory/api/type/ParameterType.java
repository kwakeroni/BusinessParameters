package be.kwakeroni.parameters.backend.inmemory.api.type;

import java.util.function.Function;

/**
 * (C) 2016 Maarten Van Puymbroeck
 */
public interface ParameterType<T> {

    public String convertToString(T value);

    public T convertFromString(String value);

    public static <T> ParameterType<T> of(Function<? super T, String> serializer, Function<String, ? extends T> deserializer){
        return new ParameterType<T>() {
            @Override
            public String convertToString(T value) {
                return serializer.apply(value);
            }

            @Override
            public T convertFromString(String value) {
                return deserializer.apply(value);
            }

        };
    }

}
