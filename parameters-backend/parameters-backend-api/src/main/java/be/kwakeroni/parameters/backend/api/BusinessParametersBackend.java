package be.kwakeroni.parameters.backend.api;

import be.kwakeroni.parameters.backend.api.query.BackendQuery;

import java.util.Collection;
import java.util.Map;

/**
 * (C) 2017 Maarten Van Puymbroeck
 */
public interface BusinessParametersBackend<Q, S, E> {

    public Collection<String> getGroupNames();

    public BackendGroup<Q, S, E> getGroup(String name);

    public <V> V select(BackendGroup<Q, S, E> group, BackendQuery<? extends Q, V> query);

    public <V> void update(BackendGroup<Q, S, E> group, BackendQuery<? extends Q, V> query, V value);

    public void insert(BackendGroup<Q, S, E> group, Map<String, String> entry);

}
