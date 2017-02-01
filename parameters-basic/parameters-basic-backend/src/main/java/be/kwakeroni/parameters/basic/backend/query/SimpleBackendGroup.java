package be.kwakeroni.parameters.basic.backend.query;


import be.kwakeroni.parameters.backend.api.BackendGroup;

/**
 * (C) 2016 Maarten Van Puymbroeck
 */
public interface SimpleBackendGroup<Q, S, E> extends BackendGroup<Q, S, E> {

    Q getEntryQuery();

    Q getValueQuery(String parameterName);
}
