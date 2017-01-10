package be.kwakeroni.parameters.api.client.model;

/**
 * Represents a Business Parameter.
 */
public interface Parameter<T> {

    public String getName();

    public T fromString(String value);

    public String toString(T value);

}
