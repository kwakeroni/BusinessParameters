package be.kwakeroni.parameters.backend.api;

import be.kwakeroni.parameters.backend.api.query.BackendQuery;
import be.kwakeroni.parameters.backend.api.query.BackendWireFormatterContext;

import java.util.Collection;
import java.util.Map;

/**
 * (C) 2017 Maarten Van Puymbroeck
 */
public interface BusinessParametersBackend<Q> {

    public Collection<String> getGroupNames();

    public BackendQuery<? extends Q, ?> internalizeQuery(String groupName, Object queryObject, BackendWireFormatterContext context);

    public <V> V select(String groupName, BackendQuery<? extends Q, V> query);

    public <V> void update(String groupName, BackendQuery<? extends Q, V> query, V value);

    public void insert(String groupName, Map<String, String> entry);

}
