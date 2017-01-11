package be.kwakeroni.parameters.types.support;

import be.kwakeroni.parameters.types.api.ParameterType;

import java.util.function.Function;

/**
 * (C) 2017 Maarten Van Puymbroeck
 */
class AdhocType<T> implements ParameterType<T> {

    private final Function<? super String, ? extends T> fromString;
    private final Function<? super T, String> toString;

    AdhocType(Function<? super String, ? extends T> fromString, Function<? super T, String> toString) {
        this.fromString = fromString;
        this.toString = toString;
    }

    @Override
    public T fromString(String value) {
        return fromString.apply(value);
    }

    @Override
    public String toString(T value) {
        return toString.apply(value);
    }
}
