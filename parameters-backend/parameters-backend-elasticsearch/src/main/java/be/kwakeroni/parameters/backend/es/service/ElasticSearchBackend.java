package be.kwakeroni.parameters.backend.es.service;

import be.kwakeroni.parameters.backend.api.BackendGroup;
import be.kwakeroni.parameters.backend.api.BusinessParametersBackend;
import be.kwakeroni.parameters.backend.api.query.BackendQuery;
import be.kwakeroni.parameters.backend.es.api.ElasticSearchData;
import be.kwakeroni.parameters.backend.es.api.ElasticSearchQuery;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * (C) 2017 Maarten Van Puymbroeck
 */
public class ElasticSearchBackend implements BusinessParametersBackend<ElasticSearchQuery<?>> {

    private static final Logger LOG = LoggerFactory.getLogger(ElasticSearchBackend.class);

    private final ElasticSearchClient client;
    private final Map<String, BackendGroup<ElasticSearchQuery<?>, ?, ?>> groups;

    public ElasticSearchBackend(Configuration configuration) {
        this.client = new ElasticSearchClient(configuration);
        this.groups = new HashMap<>();
    }

    public void registerGroup(BackendGroup<ElasticSearchQuery<?>, ?, ?> group) {
        this.groups.merge(group.getName(), group,
                (g1, g2) -> {
                    throw new IllegalStateException("Registered two groups for name " + g1.getName());
                });
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
    public BackendGroup<ElasticSearchQuery<?>, ?, ?> getGroup(String name) {
        return groups.get(name);
    }

    @Override
    public <V> V select(BackendGroup<ElasticSearchQuery<?>, ?, ?> group, BackendQuery<? extends ElasticSearchQuery<?>, V> query) {
        ElasticSearchData data = new ElasticSearchData() {
            @Override
            public Stream<JSONObject> query(JSONObject query) {
                return client.query(query);
            }
        };
        return ((ElasticSearchQuery<V>) query.raw()).apply(data,
                new JSONObject().put("match",
                        new JSONObject().put("_type", group.getName()))).orElse(null);
    }

    @Override
    public <V> void update(BackendGroup<ElasticSearchQuery<?>, ?, ?> group, BackendQuery<? extends ElasticSearchQuery<?>, V> query, V value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void insert(BackendGroup<ElasticSearchQuery<?>, ?, ?> group, Map<String, String> entry) {
        throw new UnsupportedOperationException();
    }
}
