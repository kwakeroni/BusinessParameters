package be.kwakeroni.parameters.types.api;

/**
 * (C) 2017 Maarten Van Puymbroeck
 */
public interface ParameterType<T> {

    public String toString(T value);
    public T fromString(String value);

}
