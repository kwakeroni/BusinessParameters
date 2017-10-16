package be.kwakeroni.parameters.backend.api;

import be.kwakeroni.parameters.backend.api.query.BackendQuery;
import be.kwakeroni.parameters.backend.api.query.BackendWireFormatterContext;
import be.kwakeroni.parameters.definition.api.ParameterGroupDefinition;

import java.util.Collection;
import java.util.Map;
import java.util.stream.Collector;

/**
 * (C) 2017 Maarten Van Puymbroeck
 */
public interface BusinessParametersBackend<Q> {

    public Collection<String> getGroupNames();

    public ParameterGroupDefinition getDefinition(String groupName);

    public BackendQuery<? extends Q, ?> internalizeQuery(String groupName, Object queryObject, BackendWireFormatterContext context);

    public <V> V select(String groupName, BackendQuery<? extends Q, V> query);

    public <V> void update(String groupName, BackendQuery<? extends Q, V> query, V value);

    public void update(String groupName, String id, Map<String, String> entry);

    public void insert(String groupName, Map<String, String> entry);

    public <R> R exportEntries(String groupName, Collector<? super BackendEntry, ?, R> collector);
}
