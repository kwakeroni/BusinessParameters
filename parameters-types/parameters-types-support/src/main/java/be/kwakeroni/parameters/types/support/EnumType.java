package be.kwakeroni.parameters.types.support;

import be.kwakeroni.parameters.types.api.ParameterType;

/**
 * (C) 2017 Maarten Van Puymbroeck
 */
class EnumType<E extends Enum<E>> implements ParameterType<E> {

    private final Class<E> enumClass;

    EnumType(Class<E> enumClass) {
        this.enumClass = enumClass;
    }

    @Override
    public E fromString(String value) {
        return Enum.valueOf(enumClass, value);
    }

    @Override
    public String toString(E value) {
        return value.name();
    }
}
