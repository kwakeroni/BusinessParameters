package be.kwakeroni.parameters.basic.backend.query;

import be.kwakeroni.parameters.backend.api.BackendGroup;

/**
 * (C) 2016 Maarten Van Puymbroeck
 */
public interface RangedBackendGroup<Q, E, S> extends BackendGroup<Q, E, S> {

    Q getEntryQuery(String value, Q subQuery);

    BackendGroup<Q, E, S> getSubGroup();

}
