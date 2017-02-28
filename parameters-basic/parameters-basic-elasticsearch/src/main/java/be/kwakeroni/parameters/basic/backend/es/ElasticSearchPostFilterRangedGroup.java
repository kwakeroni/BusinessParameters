package be.kwakeroni.parameters.basic.backend.es;

import be.kwakeroni.parameters.backend.api.BackendGroup;
import be.kwakeroni.parameters.backend.es.api.ElasticSearchCriteria;
import be.kwakeroni.parameters.backend.es.api.ElasticSearchData;
import be.kwakeroni.parameters.backend.es.api.ElasticSearchEntry;
import be.kwakeroni.parameters.backend.es.api.ElasticSearchQuery;
import be.kwakeroni.parameters.basic.backend.query.RangedBackendGroup;
import be.kwakeroni.parameters.basic.backend.query.support.IntermediaryBackendGroupSupport;
import be.kwakeroni.parameters.basic.backend.query.support.IntermediateBackendQuerySupport;
import be.kwakeroni.parameters.basic.type.Range;
import be.kwakeroni.parameters.types.api.ParameterType;
import org.json.JSONObject;

import java.util.Optional;
import java.util.function.Predicate;

/**
 * (C) 2017 Maarten Van Puymbroeck
 */
public class ElasticSearchPostFilterRangedGroup
        extends IntermediaryBackendGroupSupport<ElasticSearchQuery<?>, Object, ElasticSearchEntry>
        implements RangedBackendGroup<ElasticSearchQuery<?>, Object, ElasticSearchEntry> {

    private final String rangeParameterName;
    private final ParameterType<Range<String>> rangeType;


    public ElasticSearchPostFilterRangedGroup(String rangeParameterName, ParameterType<Range<String>> rangeType, BackendGroup<ElasticSearchQuery<?>, Object, ElasticSearchEntry> subGroup) {
        super(subGroup);
        this.rangeParameterName = rangeParameterName;
        this.rangeType = rangeType;
    }

    @Override
    public ElasticSearchQuery<?> getEntryQuery(String value, ElasticSearchQuery<?> subQuery) {
        return new ElasticSearchPostFilterQuery<>(
                entryWithRangeContaining(value), subQuery
        );
    }

    private Predicate<JSONObject> entryWithRangeContaining(String value) {
        return entry -> getRange(entry).contains(value);
    }

    private Range<String> getRange(JSONObject entry) {
        return this.rangeType.fromString(entry.getString(rangeParameterName));
    }

    @Override
    public void validateNewEntry(ElasticSearchEntry entry, Object storage) {
        throw new UnsupportedOperationException();
    }

    private static final class ElasticSearchPostFilterQuery<T>
            extends IntermediateBackendQuerySupport<ElasticSearchQuery<T>, T>
            implements ElasticSearchQuery<T> {

        private final Predicate<JSONObject> filter;

        public ElasticSearchPostFilterQuery(Predicate<JSONObject> filter, ElasticSearchQuery<T> subQuery) {
            super(subQuery);
            this.filter = filter;
        }

        @Override
        public Optional<T> apply(ElasticSearchData data, ElasticSearchCriteria criteria) {
            ElasticSearchData filteredData = (query, pageSize) -> data.query(query, pageSize).filter(filter);
            return getSubQuery().raw().apply(filteredData, criteria);
        }

    }

}
