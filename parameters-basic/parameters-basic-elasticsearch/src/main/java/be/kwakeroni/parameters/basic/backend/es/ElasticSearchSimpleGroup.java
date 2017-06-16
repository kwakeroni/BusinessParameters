package be.kwakeroni.parameters.basic.backend.es;

import be.kwakeroni.parameters.backend.es.api.ElasticSearchData;
import be.kwakeroni.parameters.backend.es.api.ElasticSearchEntry;
import be.kwakeroni.parameters.backend.es.api.ElasticSearchGroup;
import be.kwakeroni.parameters.backend.es.api.ElasticSearchQuery;
import be.kwakeroni.parameters.basic.backend.query.support.SimpleBackendGroupSupport;

import java.util.Map;
import java.util.Set;

/**
 * (C) 2017 Maarten Van Puymbroeck
 */
public class ElasticSearchSimpleGroup extends SimpleBackendGroupSupport<ElasticSearchQuery<?>, ElasticSearchData, ElasticSearchEntry>
        implements ElasticSearchGroup {

    public ElasticSearchSimpleGroup(String name, String... parameters) {
        super(name, parameters);
    }

    public ElasticSearchSimpleGroup(String name, Set<String> parameters) {
        super(name, parameters);
    }

    @Override
    protected boolean hasAnyEntry(ElasticSearchData storage) {
        return storage.findAll(1).findAny().isPresent();
    }

    @Override
    protected Map<String, String> asMap(ElasticSearchEntry entry) {
        return entry.toParameterMap();
    }

    @Override
    public ElasticSearchQuery<?> getEntryQuery() {
        return EntryElasticSearchQuery.INSTANCE;
    }

    @Override
    public ElasticSearchQuery<?> getValueQuery(String parameterName) {
        return new ValueElasticSearchQuery(parameterName);
    }

    @Override
    public ElasticSearchEntry prepareAndValidateNewEntry(ElasticSearchEntry entry, ElasticSearchData storage) {
        return validateNewEntry(entry, storage);
    }

    @Override
    public String toString() {
        return "simple(ES " + getParameterNames() + ")";
    }

}
