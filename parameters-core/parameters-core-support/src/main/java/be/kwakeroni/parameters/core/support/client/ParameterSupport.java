package be.kwakeroni.parameters.core.support.client;

import be.kwakeroni.parameters.client.api.model.Parameter;
import be.kwakeroni.parameters.types.api.ParameterType;
import be.kwakeroni.parameters.types.support.JavaLangType;

public class ParameterSupport<T> implements Parameter<T> {

    private final String name;
    private final ParameterType<T> type;

    public ParameterSupport(String name, ParameterType<T> type) {
        this.name = name;
        this.type = type;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public T fromString(String value) {
        return type.fromString(value);
    }

    @Override
    public String toString(T value) {
        return type.toString(value);
    }

    public static Parameter<String> ofString(String name) {
        return new ParameterSupport<>(name, JavaLangType.STRING);
    }

    public static Parameter<Integer> ofInt(String name) {
        return new ParameterSupport<>(name, JavaLangType.INT);
    }

    public static Parameter<Long> ofLong(String name) {
        return new ParameterSupport<>(name, JavaLangType.LONG);
    }

    public static Parameter<Boolean> ofBoolean(String name) {
        return new ParameterSupport<>(name, JavaLangType.BOOLEAN);
    }

    public static Parameter<Character> ofChar(String name) {
        return new ParameterSupport<>(name, JavaLangType.CHAR);
    }

}
