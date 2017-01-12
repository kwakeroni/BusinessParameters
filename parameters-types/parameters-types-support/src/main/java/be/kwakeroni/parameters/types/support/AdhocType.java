package be.kwakeroni.parameters.types.support;

import be.kwakeroni.parameters.types.api.ParameterType;

import java.util.Objects;
import java.util.function.Function;

/**
 * (C) 2017 Maarten Van Puymbroeck
 */
class AdhocType<T> implements ParameterType<T> {

    private final Function<? super String, ? extends T> fromString;
    private final Function<? super T, String> toString;
    private final Class<T> type;

    AdhocType(Function<? super String, ? extends T> fromString, Function<? super T, String> toString) {
        this(null, fromString, toString);
    }

    AdhocType(Class<T> type, Function<? super String, ? extends T> fromString, Function<? super T, String> toString) {
        this.fromString = Objects.requireNonNull(fromString);
        this.toString = Objects.requireNonNull(toString);
        this.type = type;
    }

    @Override
    public T fromString(String value) {
        return fromString.apply(value);
    }

    @Override
    public String toString(T value) {
        return toString.apply(value);
    }

    @Override
    public String toString() {
        return "ADHOC[" + ((type == null) ? "?" : type.getSimpleName()) + "]";
    }
}
