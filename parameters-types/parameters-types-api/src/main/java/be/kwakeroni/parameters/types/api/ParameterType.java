package be.kwakeroni.parameters.types.api;

/**
 * Represents the type of a business parameter with values of the given type {@code T}.
 * A ParameterType can transform such values from and to String representations.
 * @param <T> Type of the values of this parameter type.
 */
public interface ParameterType<T> {

    public String toString(T value);
    public T fromString(String value);

}
