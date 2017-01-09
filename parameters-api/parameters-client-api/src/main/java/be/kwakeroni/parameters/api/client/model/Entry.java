package be.kwakeroni.parameters.api.client.model;

public interface Entry {

    <T> T getValue(Parameter<T> parameter);

}
