package be.kwakeroni.parameters.types.support;

import be.kwakeroni.parameters.types.api.ParameterType;

import java.util.Comparator;

/**
 * Represents a {@link ParameterType} that maps a custom type {@code T} to a standard java type {@code B}.
 * @param <T> Type of the values of this parameter type.
 * @param <B> Standard Java type backing this ParameterType.
 */
public interface BasicType<T, B> extends ParameterType<T>, Comparator<B> {

    /**
     * @return The standard Java type backing this ParameterType
     */
    public JavaLangType asBasicType();

    public B toBasic(T value);

    public T fromBasic(B value);

    @Override
    default int compare(B o1, B o2) {
        return asBasicType().compare(o1, o2);
    }
}
