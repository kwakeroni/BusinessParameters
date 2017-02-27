package be.kwakeroni.parameters.basic.backend.es;

import be.kwakeroni.parameters.backend.api.BackendGroup;
import be.kwakeroni.parameters.backend.es.api.ElasticSearchCriteria;
import be.kwakeroni.parameters.backend.es.api.ElasticSearchData;
import be.kwakeroni.parameters.backend.es.api.ElasticSearchEntry;
import be.kwakeroni.parameters.backend.es.api.ElasticSearchQuery;
import be.kwakeroni.parameters.basic.backend.query.MappedBackendGroup;
import be.kwakeroni.parameters.basic.backend.query.support.IntermediaryBackendGroupSupport;
import be.kwakeroni.parameters.basic.backend.query.support.IntermediateBackendQuerySupport;

import java.util.Optional;

/**
 * (C) 2017 Maarten Van Puymbroeck
 */
public class ElasticSearchMappedGroup
        extends IntermediaryBackendGroupSupport<ElasticSearchQuery<?>, Object, ElasticSearchEntry>
        implements MappedBackendGroup<ElasticSearchQuery<?>, Object, ElasticSearchEntry> {

    private final String keyParameterName;

    public ElasticSearchMappedGroup(String keyParameterName, BackendGroup<ElasticSearchQuery<?>, Object, ElasticSearchEntry> subGroup) {
        super(subGroup);
        this.keyParameterName = keyParameterName;
    }

    @Override
    public ElasticSearchQuery<?> getEntryQuery(String keyValue, ElasticSearchQuery<?> subQuery) {
        return new ElasticSearchMappedQuery<>(keyValue, subQuery);
    }

    @Override
    public void validateNewEntry(ElasticSearchEntry entry, Object storage) {
        throw new UnsupportedOperationException();
    }

    private final class ElasticSearchMappedQuery<T>
            extends IntermediateBackendQuerySupport<ElasticSearchQuery<T>, T>
            implements ElasticSearchQuery<T> {

        private final String keyValue;

        public ElasticSearchMappedQuery(String keyValue, ElasticSearchQuery<T> subQuery) {
            super(subQuery);
            this.keyValue = keyValue;
        }

        @Override
        public Optional<T> apply(ElasticSearchData data, ElasticSearchCriteria criteria) {
            criteria.addParameterMatch(keyParameterName, this.keyValue);
            return getSubQuery().raw().apply(data, criteria);
        }

    }
}
