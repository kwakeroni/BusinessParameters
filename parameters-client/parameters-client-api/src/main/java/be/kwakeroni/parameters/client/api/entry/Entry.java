package be.kwakeroni.parameters.client.api.entry;

import be.kwakeroni.parameters.api.client.model.Parameter;

public interface Entry {

    <T> T getValue(Parameter<T> parameter);

}
