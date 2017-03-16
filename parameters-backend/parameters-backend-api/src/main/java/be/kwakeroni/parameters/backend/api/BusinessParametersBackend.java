package be.kwakeroni.parameters.backend.api;

import be.kwakeroni.parameters.backend.api.query.BackendQuery;

import java.util.Collection;
import java.util.Map;

/**
 * (C) 2017 Maarten Van Puymbroeck
 */
public interface BusinessParametersBackend<Q, BG extends BackendGroup<Q, ?, ?>> {

    public Collection<String> getGroupNames();

    public BG getGroup(String name);

    public <V> V select(BG group, BackendQuery<? extends Q, V> query);

    public <V> void update(BG group, BackendQuery<? extends Q, V> query, V value);

    public void insert(BG group, Map<String, String> entry);

}
