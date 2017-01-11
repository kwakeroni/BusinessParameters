package be.kwakeroni.parameters.basic.backend.query;

import be.kwakeroni.parameters.backend.api.BackendGroup;

/**
 * (C) 2016 Maarten Van Puymbroeck
 */
public interface MappedBackendGroup<Q> extends BackendGroup<Q> {

    Q getEntryQuery(String keyValue, Q subQuery);

    BackendGroup<Q> getSubGroup();

}
