package be.kwakeroni.parameters.backend.api;

import be.kwakeroni.parameters.backend.api.query.BackendQuery;

import java.util.Collection;
import java.util.Map;

/**
 * (C) 2017 Maarten Van Puymbroeck
 */
public interface BusinessParametersBackend<Q> {

    public Collection<String> getGroupNames();

    public BackendGroup<Q, ?, ?> getGroup(String name);

    public <V> V select(BackendGroup<Q, ?, ?> group, BackendQuery<? extends Q, V> query);

    public <V> void update(BackendGroup<Q, ?, ?> group, BackendQuery<? extends Q, V> query, V value);

    public void insert(BackendGroup<Q, ?, ?> group, Map<String, String> entry);

}
