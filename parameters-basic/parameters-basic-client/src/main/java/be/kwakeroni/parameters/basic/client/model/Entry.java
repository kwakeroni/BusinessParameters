package be.kwakeroni.parameters.basic.client.model;

import be.kwakeroni.parameters.client.api.model.Parameter;

import java.util.Map;

/**
 * (C) 2016 Maarten Van Puymbroeck
 */
public interface Entry {

    <T> T getValue(Parameter<T> parameter);

    boolean hasValue(Parameter<?> parameter);

    Map<String, String> toMap();

}
