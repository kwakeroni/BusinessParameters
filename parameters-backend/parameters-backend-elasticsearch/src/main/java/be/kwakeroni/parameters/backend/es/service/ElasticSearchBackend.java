package be.kwakeroni.parameters.backend.es.service;

import be.kwakeroni.parameters.backend.api.BusinessParametersBackend;
import be.kwakeroni.parameters.backend.api.query.BackendQuery;
import be.kwakeroni.parameters.backend.api.query.BackendWireFormatterContext;
import be.kwakeroni.parameters.backend.es.api.*;
import be.kwakeroni.parameters.backend.es.factory.ElasticSearchGroupFactoryContext;
import be.kwakeroni.parameters.definition.api.ParameterGroupDefinition;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * (C) 2017 Maarten Van Puymbroeck
 */
public class ElasticSearchBackend implements BusinessParametersBackend<ElasticSearchQuery<?>> {

    private static final Logger LOG = LoggerFactory.getLogger(ElasticSearchBackend.class);

    private final ElasticSearchClient client;
    private final ElasticSearchGroupFactoryContext factoryContext;
    private final Map<String, ElasticSearchGroup> groups;
    private final Supplier<Stream<ParameterGroupDefinition>> definitions;

    public ElasticSearchBackend(Configuration configuration, ElasticSearchGroupFactoryContext factoryContext, Supplier<Stream<ParameterGroupDefinition>> definitions) {
        this.client = new ElasticSearchClient(configuration);
        this.factoryContext = factoryContext;
        this.definitions = definitions;
        this.groups = new HashMap<>();
    }

    private synchronized ElasticSearchGroup getGroup(String name) {
        return this.groups.computeIfAbsent(name, this::defineGroup);
    }

    private ElasticSearchGroup defineGroup(String name) {
        return this.definitions.get()
                .filter(definition -> name.equals(definition.getName()))
                .findAny()
                .map(this::defineGroup)
                .orElseThrow(() -> new IllegalStateException("No definition found for group " + name));
    }

    private ElasticSearchGroup defineGroup(ParameterGroupDefinition definition) {
        return definition.apply(this.factoryContext);
    }

    private void unregisterGroup(String name) {
        this.groups.remove(name);
    }

    @Override
    public Collection<String> getGroupNames() {
        Stream<JSONObject> stream = client.getAggregation("group_by_type", new JSONObject().put("field", "_type"));
        Collection<String> inDB = stream.map(o -> o.getString("key")).collect(Collectors.toList());
        return definitions.get()
                .map(ParameterGroupDefinition::getName)
                .peek(name -> {
                    if (!inDB.contains(name)) {
                        LOG.warn("Registered group without data: " + name);
                    }
                })
                .collect(Collectors.toList());
    }

    @Override
    public ParameterGroupDefinition getDefinition(String groupName) {
        return getGroup(groupName).getDefinition();
    }

    @Override
    public BackendQuery<? extends ElasticSearchQuery<?>, ?> internalizeQuery(String groupName, Object queryObject, BackendWireFormatterContext context) {
        return getGroup(groupName).internalize(queryObject, context);
    }

    private ElasticSearchData data = new ElasticSearchData() {
        @Override
        public Stream<ElasticSearchEntry> findAll(Consumer<ElasticSearchCriteria> criteriaBuilder, int pageSize) {
            ElasticSearchCriteria criteria = new DefaultElasticSearchCriteria();
            criteriaBuilder.accept(criteria);
            return client.query(criteria.toJSONObject(), pageSize);
        }
    };

    private ElasticSearchData getDataForGroup(String name) {
        return data.with(criteria -> criteria.inGroup(name));
    }

    @Override
    public <V> V select(String group, BackendQuery<? extends ElasticSearchQuery<?>, V> query) {
        return ((ElasticSearchQuery<V>) query.raw()).apply(getDataForGroup(group)).orElse(null);
    }

    @Override
    public <V> void update(String group, BackendQuery<? extends ElasticSearchQuery<?>, V> query, V value) {
        doUpdate(group, (ElasticSearchQuery<V>) query.raw(), value);
    }

    private <V> void doUpdate(String group, ElasticSearchQuery<V> query, V value) {
        ElasticSearchData groupData = getDataForGroup(group);
        EntryModification modification = query.getEntryModification(value, groupData);
        ElasticSearchEntry original = modification.getOriginalEntry();
        ElasticSearchEntry newEntry = original.copy();
        modification.modify(newEntry);
        newEntry = getGroup(group).prepareAndValidateNewEntry(newEntry, groupData.with(criteria -> criteria.addParameterNotMatch("_id", modification.getOriginalEntry().getId())));
        client.update(group, newEntry);
    }

    @Override
    public void insert(String group, Map<String, String> entry) {
        ElasticSearchData groupData = getDataForGroup(group);
        ElasticSearchEntry newEntry = new DefaultElasticSearchEntry(entry);
        newEntry = getGroup(group).prepareAndValidateNewEntry(newEntry, groupData);
        client.insert(group, newEntry);
    }
}
