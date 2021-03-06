package be.kwakeroni.parameters.basic.backend.query;


import be.kwakeroni.parameters.backend.api.BackendGroup;

/**
 * (C) 2016 Maarten Van Puymbroeck
 */
public interface SimpleBackendGroup<Q> extends BackendGroup<Q> {

    Q getEntryQuery();

    Q getValueQuery(String parameterName);
}
