package be.kwakeroni.parameters.client.api.model;

/**
 * Represents a Business Parameter.
 */
public interface Parameter<T> {

    public String getName();

    public T fromString(String value);

    public String toString(T value);

}
