package be.kwakeroni.parameters.basic.backend.es;

import be.kwakeroni.parameters.backend.api.BackendGroup;
import be.kwakeroni.parameters.backend.es.api.ElasticSearchCriteria;
import be.kwakeroni.parameters.backend.es.api.ElasticSearchData;
import be.kwakeroni.parameters.backend.es.api.ElasticSearchDataType;
import be.kwakeroni.parameters.backend.es.api.ElasticSearchEntry;
import be.kwakeroni.parameters.backend.es.api.ElasticSearchQuery;
import be.kwakeroni.parameters.basic.backend.query.RangedBackendGroup;
import be.kwakeroni.parameters.basic.backend.query.support.IntermediaryBackendGroupSupport;
import be.kwakeroni.parameters.basic.backend.query.support.IntermediateBackendQuerySupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;
import java.util.function.Function;

/**
 * (C) 2017 Maarten Van Puymbroeck
 */
public class ElasticSearchQueryBasedRangedGroup
        extends IntermediaryBackendGroupSupport<ElasticSearchQuery<?>, Object, ElasticSearchEntry>
        implements RangedBackendGroup<ElasticSearchQuery<?>, Object, ElasticSearchEntry> {

    private static final Logger LOG = LoggerFactory.getLogger(ElasticSearchQueryBasedRangedGroup.class);

    private final String rangeParameterName;
    private final ElasticSearchDataType<?> dataType;
    private final Function<String, ?> converter;

    public <T> ElasticSearchQueryBasedRangedGroup(String rangeParameterName, ElasticSearchDataType<T> dataType, Function<String, T> converter, BackendGroup<ElasticSearchQuery<?>, Object, ElasticSearchEntry> subGroup) {
        super(subGroup);
        this.rangeParameterName = rangeParameterName;
        this.dataType = dataType;
        this.converter = converter;
    }

    @Override
    public ElasticSearchQuery<?> getEntryQuery(String value, ElasticSearchQuery<?> subQuery) {
        return new ElasticSearchRangedQuery<>(value, subQuery);
    }

    @Override
    public void validateNewEntry(ElasticSearchEntry entry, Object storage) {
        throw new UnsupportedOperationException();
    }

    public static String getFromParameter(String parameterName) {
        return parameterName + "_range_from";
    }

    public static String getToParameter(String parameterName) {
        return parameterName + "_range_to";
    }

    private final class ElasticSearchRangedQuery<T>
            extends IntermediateBackendQuerySupport<ElasticSearchQuery<T>, T>
            implements ElasticSearchQuery<T> {

        private final String value;

        public ElasticSearchRangedQuery(String value, ElasticSearchQuery<T> subQuery) {
            super(subQuery);
            this.value = value;
        }

        @Override
        public Optional<T> apply(ElasticSearchData data, ElasticSearchCriteria criteria) {
            String fromParameter = getFromParameter(rangeParameterName);
            String toParameter = getToParameter(rangeParameterName);

            ElasticSearchData verifyingData = (query, pageSize) -> data.query(query, pageSize).peek(
                    jo -> {
                        if ((!jo.keySet().contains(fromParameter)) ||
                                (!jo.keySet().contains(toParameter))) {
                            LOG.warn("Missing range limit parameters {} and/or {}",
                                    fromParameter, toParameter);
                        }
                    }
            ).peek(jo -> {
                jo.remove(fromParameter);
                jo.remove(toParameter);
            });

            Object comparedValue = converter.apply(value);

            criteria.addParameterComparison(fromParameter, "lte", comparedValue);
            criteria.addParameterComparison(toParameter, "gt", comparedValue);
            return getSubQuery().raw().apply(verifyingData, criteria);
        }

    }

}
