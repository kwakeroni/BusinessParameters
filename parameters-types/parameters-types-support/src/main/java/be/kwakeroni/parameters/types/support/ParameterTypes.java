package be.kwakeroni.parameters.types.support;

import be.kwakeroni.parameters.types.api.ParameterType;

import java.util.function.Function;

/**
 * (C) 2017 Maarten Van Puymbroeck
 */
public interface ParameterTypes {

    public static ParameterType<String> STRING = JavaLangType.STRING;

    public static <E extends Enum<E>> ParameterType<E> ofEnum(Class<E> type){
        return new EnumType<>(type);
    }

    public static <T> ParameterType<T> of(Class<T> type, Function<? super String, ? extends T> fromString, Function<? super T, String> toString){
        return new AdhocType<>(type, fromString, toString);
    }

    public static <T> ParameterType<T> of(Function<? super String, ? extends T> fromString, Function<? super T, String> toString){
        return new AdhocType<>(fromString, toString);
    }

}
