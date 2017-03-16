package be.kwakeroni.parameters.basic.backend.query;

import be.kwakeroni.parameters.backend.api.BackendGroup;

/**
 * (C) 2016 Maarten Van Puymbroeck
 */
public interface MappedBackendGroup<Q, BG extends BackendGroup<Q, ?, ?>, S, E> extends BackendGroup<Q, S, E> {

    Q getEntryQuery(String keyValue, Q subQuery);

    BG getSubGroup();

}
