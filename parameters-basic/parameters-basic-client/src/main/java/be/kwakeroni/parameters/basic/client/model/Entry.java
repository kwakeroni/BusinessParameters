package be.kwakeroni.parameters.basic.client.model;

import be.kwakeroni.parameters.api.client.model.Parameter;

/**
 * (C) 2016 Maarten Van Puymbroeck
 */
public interface Entry {

    <T> T getValue(Parameter<T> parameter);

}
