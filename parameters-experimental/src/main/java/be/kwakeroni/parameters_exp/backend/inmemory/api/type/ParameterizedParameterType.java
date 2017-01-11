package be.kwakeroni.parameters_exp.backend.inmemory.api.type;

import java.util.function.Function;

/**
 * (C) 2016 Maarten Van Puymbroeck
 */
public interface ParameterizedParameterType<T> extends ParameterType<T> {

    public <P> ParameterType<P> getTypeArgument(int index);

    public static <T> ParameterizedParameterType<T> of(Function<? super T, String> serializer,
                                                       Function<String, ? extends T> deserializer,
                                                       ParameterType<?>... typeArgs){
        return new ParameterizedParameterType<T>() {
            @Override
            public String convertToString(T value) {
                return serializer.apply(value);
            }

            @Override
            public T convertFromString(String value) {
                return deserializer.apply(value);
            }

            @Override
            public <P> ParameterType<P> getTypeArgument(int index) {
                return (ParameterType<P>) typeArgs[index];
            }
        };
    }
}
