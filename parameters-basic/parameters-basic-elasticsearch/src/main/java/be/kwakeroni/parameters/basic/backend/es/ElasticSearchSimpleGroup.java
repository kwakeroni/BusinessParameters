package be.kwakeroni.parameters.basic.backend.es;

import be.kwakeroni.parameters.backend.es.api.ElasticSearchEntry;
import be.kwakeroni.parameters.backend.es.api.ElasticSearchQuery;
import be.kwakeroni.parameters.basic.backend.query.support.SimpleBackendGroupSupport;

import java.util.Map;
import java.util.Set;

/**
 * (C) 2017 Maarten Van Puymbroeck
 */
public class ElasticSearchSimpleGroup extends SimpleBackendGroupSupport<ElasticSearchQuery<?>, Object, ElasticSearchEntry> {

    public ElasticSearchSimpleGroup(String name, String... parameters) {
        super(name, parameters);
    }

    public ElasticSearchSimpleGroup(String name, Set<String> parameters) {
        super(name, parameters);
    }

    @Override
    protected boolean hasAnyEntry(Object storage) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected Map<String, String> asMap(ElasticSearchEntry entry) {
        return entry.toMap();
    }

    @Override
    public ElasticSearchQuery<?> getEntryQuery() {
        return EntryElasticSearchQuery.INSTANCE;
    }

    @Override
    public ElasticSearchQuery<?> getValueQuery(String parameterName) {
        return new ValueElasticSearchQuery(parameterName);
    }
}
