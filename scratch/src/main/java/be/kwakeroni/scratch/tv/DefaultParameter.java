package be.kwakeroni.scratch.tv;

import be.kwakeroni.parameters.api.client.model.Parameter;

import java.util.function.Function;

/**
 * (C) 2016 Maarten Van Puymbroeck
 */
public final class DefaultParameter<T> implements Parameter<T> {

    private final String name;
    private final Function<T, String> toString;
    private final Function<String, T> fromString;

    public DefaultParameter(String name, Function<String, T> fromString, Function<T, String> toString) {
        this.fromString = fromString;
        this.name = name;
        this.toString = toString;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public T fromString(String value) {
        return this.fromString.apply(value);
    }

    @Override
    public String toString(T value) {
        return this.toString.apply(value);
    }
}
