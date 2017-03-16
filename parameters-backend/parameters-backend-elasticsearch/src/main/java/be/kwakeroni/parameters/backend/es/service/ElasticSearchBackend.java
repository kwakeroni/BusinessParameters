package be.kwakeroni.parameters.backend.es.service;

import be.kwakeroni.parameters.backend.api.BackendGroup;
import be.kwakeroni.parameters.backend.api.BusinessParametersBackend;
import be.kwakeroni.parameters.backend.api.query.BackendQuery;
import be.kwakeroni.parameters.backend.es.api.ElasticSearchCriteria;
import be.kwakeroni.parameters.backend.es.api.ElasticSearchData;
import be.kwakeroni.parameters.backend.es.api.ElasticSearchEntry;
import be.kwakeroni.parameters.backend.es.api.ElasticSearchQuery;
import be.kwakeroni.parameters.backend.es.api.EntryModification;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * (C) 2017 Maarten Van Puymbroeck
 */
public class ElasticSearchBackend implements BusinessParametersBackend<ElasticSearchQuery<?>, ElasticSearchData, ElasticSearchEntry> {

    private static final Logger LOG = LoggerFactory.getLogger(ElasticSearchBackend.class);

    private final ElasticSearchClient client;
    private final Map<String, BackendGroup<ElasticSearchQuery<?>, ElasticSearchData, ElasticSearchEntry>> groups;

    public ElasticSearchBackend(Configuration configuration) {
        this.client = new ElasticSearchClient(configuration);
        this.groups = new HashMap<>();
    }

    public void registerGroup(BackendGroup<ElasticSearchQuery<?>, ElasticSearchData, ElasticSearchEntry> group) {
        this.groups.merge(group.getName(), group,
                (g1, g2) -> {
                    throw new IllegalStateException("Registered two groups for name " + g1.getName());
                });
    }

    public void unregisterGroup(String name) {
        this.groups.remove(name);
    }

    @Override
    public Collection<String> getGroupNames() {
        Stream<JSONObject> stream = client.getAggregation("group_by_type", new JSONObject().put("field", "_type"));
        Collection<String> inDB = stream.map(o -> o.getString("key")).collect(Collectors.toList());
        return groups.keySet()
                .stream()
                .peek(key -> {
                    if (!inDB.contains(key)) {
                        LOG.warn("Registered group without data: " + key);
                    }
                })
                .collect(Collectors.toList());
    }

    @Override
    public BackendGroup<ElasticSearchQuery<?>, ElasticSearchData, ElasticSearchEntry> getGroup(String name) {
        return groups.get(name);
    }

    private ElasticSearchData data = new ElasticSearchData() {
        @Override
        public Stream<ElasticSearchEntry> findAll(Consumer<ElasticSearchCriteria> criteriaBuilder, int pageSize) {
            ElasticSearchCriteria criteria = new DefaultElasticSearchCriteria();
            criteriaBuilder.accept(criteria);
            return client.query(criteria.toJSONObject(), pageSize);
        }
    };

    private ElasticSearchData getDataForGroup(String name){
        return data.with(criteria -> criteria.inGroup(name));
    }

    @Override
    public <V> V select(BackendGroup<ElasticSearchQuery<?>, ElasticSearchData, ElasticSearchEntry> group, BackendQuery<? extends ElasticSearchQuery<?>, V> query) {
        return ((ElasticSearchQuery<V>) query.raw()).apply(getDataForGroup(group.getName())).orElse(null);
    }

    @Override
    public <V> void update(BackendGroup<ElasticSearchQuery<?>, ElasticSearchData, ElasticSearchEntry> group, BackendQuery<? extends ElasticSearchQuery<?>, V> query, V value) {
        doUpdate(group, (ElasticSearchQuery<V>) query.raw(), value);
    }

    private <V> void doUpdate(BackendGroup<ElasticSearchQuery<?>, ElasticSearchData, ElasticSearchEntry> group, ElasticSearchQuery<V> query, V value){
        ElasticSearchData groupData = getDataForGroup(group.getName());
        EntryModification modification = query.getEntryModification(value, groupData);
        ElasticSearchEntry original = modification.getOriginalEntry();
        ElasticSearchEntry newEntry = original.copy();
        modification.modify(newEntry);
        newEntry = group.prepareAndValidateNewEntry(newEntry, groupData.with(criteria -> criteria.addParameterNotMatch("_id", modification.getOriginalEntry().getId())));
        client.update(group.getName(), newEntry);
    }

    @Override
    public void insert(BackendGroup<ElasticSearchQuery<?>, ElasticSearchData, ElasticSearchEntry> group, Map<String, String> entry) {
        ElasticSearchData groupData = getDataForGroup(group.getName());
        ElasticSearchEntry newEntry = new DefaultElasticSearchEntry(entry);
        newEntry = group.prepareAndValidateNewEntry(newEntry, groupData);
        client.insert(group.getName(), newEntry);
    }
}
