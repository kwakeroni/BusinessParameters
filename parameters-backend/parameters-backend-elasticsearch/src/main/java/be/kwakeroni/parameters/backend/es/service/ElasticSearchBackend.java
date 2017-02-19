package be.kwakeroni.parameters.backend.es.service;

import be.kwakeroni.parameters.backend.api.BackendGroup;
import be.kwakeroni.parameters.backend.api.BusinessParametersBackend;
import be.kwakeroni.parameters.backend.api.query.BackendQuery;
import be.kwakeroni.parameters.backend.es.api.ElasticSearchQuery;

import java.util.Collection;
import java.util.Map;

/**
 * (C) 2017 Maarten Van Puymbroeck
 */
public class ElasticSearchBackend implements BusinessParametersBackend<ElasticSearchQuery<?>> {

    ElasticSearchClient client = new ElasticSearchClient();

    public ElasticSearchBackend(Configuration configuration) {
    }

    @Override
    public Collection<String> getGroupNames() {
        throw new UnsupportedOperationException();
    }

    @Override
    public BackendGroup<ElasticSearchQuery<?>, ?, ?> getGroup(String name) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <V> V select(BackendGroup<ElasticSearchQuery<?>, ?, ?> group, BackendQuery<? extends ElasticSearchQuery<?>, V> query) {
        throw new UnsupportedOperationException();
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
