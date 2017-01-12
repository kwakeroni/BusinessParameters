package be.kwakeroni.parameters.types.support;

import be.kwakeroni.parameters.types.api.ParameterType;

import java.util.Objects;

/**
 * (C) 2017 Maarten Van Puymbroeck
 */
class EnumType<E extends Enum<E>> implements ParameterType<E> {

    private final Class<E> enumClass;

    EnumType(Class<E> enumClass) {
        this.enumClass = Objects.requireNonNull(enumClass);
    }

    @Override
    public E fromString(String value) {
        return Enum.valueOf(enumClass, value);
    }

    @Override
    public String toString(E value) {
        return value.name();
    }

    @Override
    public String toString() {
        return "ENUM[" + enumClass.getSimpleName() + "]";
    }
}
