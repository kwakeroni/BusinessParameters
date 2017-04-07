package be.kwakeroni.parameters.basic.backend.es;

import be.kwakeroni.parameters.backend.api.BackendGroup;
import be.kwakeroni.parameters.backend.es.api.ElasticSearchCriteria;
import be.kwakeroni.parameters.backend.es.api.ElasticSearchData;
import be.kwakeroni.parameters.backend.es.api.ElasticSearchDataType;
import be.kwakeroni.parameters.backend.es.api.ElasticSearchEntry;
import be.kwakeroni.parameters.backend.es.api.ElasticSearchGroup;
import be.kwakeroni.parameters.backend.es.api.ElasticSearchQuery;
import be.kwakeroni.parameters.backend.es.api.EntryModification;
import be.kwakeroni.parameters.basic.backend.query.RangedBackendGroup;
import be.kwakeroni.parameters.basic.backend.query.support.IntermediaryBackendGroupSupport;
import be.kwakeroni.parameters.basic.backend.query.support.IntermediateBackendQuerySupport;
import be.kwakeroni.parameters.basic.type.Range;
import be.kwakeroni.parameters.basic.type.Ranges;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * (C) 2017 Maarten Van Puymbroeck
 */
public class ElasticSearchQueryBasedRangedGroup
        extends IntermediaryBackendGroupSupport<ElasticSearchQuery<?>, ElasticSearchGroup>
        implements ElasticSearchGroup, RangedBackendGroup<ElasticSearchQuery<?>, ElasticSearchGroup> {

    private static final Logger LOG = LoggerFactory.getLogger(ElasticSearchQueryBasedRangedGroup.class);

    private final String rangeParameterName;
    private final ElasticSearchDataType<?> dataType;
    private final Function<String, ?> converter;

    public <T> ElasticSearchQueryBasedRangedGroup(String rangeParameterName, ElasticSearchDataType<T> dataType, Function<String, T> converter, ElasticSearchGroup subGroup) {
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
    public ElasticSearchEntry prepareAndValidateNewEntry(ElasticSearchEntry entry, ElasticSearchData storage) {
        String rangeString = entry.getParameter(this.rangeParameterName);

        clearMetaParams(entry);

        try {
            entry = getSubGroup().prepareAndValidateNewEntry(entry, storage.with(rangeWithOverlap(rangeString)));
        } catch (IllegalStateException exc) {
            throw new IllegalStateException(exc.getMessage() + " with range: " + this.rangeParameterName + "=" + rangeString);
        }

        addMetaParams(entry);

        return entry;
    }

    private void addMetaParams(ElasticSearchEntry entry){
        String rangeString = entry.getParameter(this.rangeParameterName);
        Range<String> range = Ranges.fromString(rangeString, Function.identity());
        entry.setMetaParameter(getFromParameter(), toJSONRepresentation(range.getFrom()));
        entry.setMetaParameter(getToParameter(), toJSONRepresentation(range.getTo()));
    }

    private <T> Object toJSONRepresentation(String valueString){
        T value = (T) converter.apply(valueString);
        return ((ElasticSearchDataType<T>) dataType).toJSONRepresentation(value);
    }

    private void clearMetaParams(ElasticSearchEntry entry){
        entry.clearParameter(getFromParameter(this.rangeParameterName));
        entry.clearParameter(getToParameter(this.rangeParameterName));
    }

    private String getFromParameter(){
        return getFromParameter(this.rangeParameterName);
    }

    private String getToParameter(){
        return getToParameter(this.rangeParameterName);
    }

    private Consumer<ElasticSearchCriteria> rangeContaining(Object comparedValue) {
        String fromParameter = getFromParameter();
        String toParameter = getToParameter();

        return criteria -> {
            criteria.addParameterComparison(fromParameter, "lte", comparedValue);
            criteria.addParameterComparison(toParameter, "gt", comparedValue);
        };
    }

    private Consumer<ElasticSearchCriteria> rangeWithOverlap(String rangeString) {
        Range<String> stringRange = Ranges.fromString(rangeString, Function.identity());
        return rangeWithOverlap(converter.apply(stringRange.getFrom()),
                converter.apply(stringRange.getTo()));
    }

    private Consumer<ElasticSearchCriteria> rangeWithOverlap(Object from, Object to) {
        String fromParameter = getFromParameter();
        String toParameter = getToParameter();

//      (otherFrom >= this.from && otherFrom < this.to)
        JSONObject fromp = new JSONObject()
                .put("range", new JSONObject()
                        .put(fromParameter, new JSONObject()
                                .put("gte", from)
                                .put("lt", to)));

//       (otherTo > this.from && otherTo < this.to)
        JSONObject top = new JSONObject()
                .put("range", new JSONObject()
                        .put(toParameter, new JSONObject()
                                .put("gt", from)
                                .put("lte", to)));

//      (this.from >= otherFrom && this.from < otherTo
        JSONObject ofromp = new JSONObject()
                .put("range", new JSONObject()
                        .put(fromParameter, new JSONObject()
                                .put("lte", from))
                        );
        JSONObject otop = new JSONObject()
                .put("range", new JSONObject()
                        .put(toParameter, new JSONObject()
                                .put("gt", from)
                        ));

        JSONObject other = new JSONObject()
                .put("bool", new JSONObject()
                    .put("must", new JSONArray()
                        .put(ofromp)
                        .put(otop)));

        JSONObject filter = new JSONObject()
                .put("bool", new JSONObject()
                        .put("should", new JSONArray()
                                .put(fromp)
                                .put(top)
                                .put(other)
                                ));

//        return (otherTo > this.from && otherTo < this.to)
//                || (otherFrom >= this.from && otherFrom < this.to)
//                || (this.from >= otherFrom && this.from < otherTo

        return criteria -> criteria.addComplexFilter(filter);
    }

    private final class ElasticSearchRangedQuery<T>
            extends IntermediateBackendQuerySupport<ElasticSearchQuery<T>, T>
            implements ElasticSearchQuery<T> {

        //        private final String value;
        private final Object comparedValue;

        public ElasticSearchRangedQuery(String value, ElasticSearchQuery<T> subQuery) {
            super(subQuery);
//            this.value = value;
            this.comparedValue = converter.apply(value);

        }

        @Override
        public Optional<T> apply(ElasticSearchData data) {
            String fromParameter = getFromParameter();
            String toParameter = getToParameter();

            ElasticSearchData verifyingData = data.withFilter(stream -> stream.peek(
                    entry -> {
                        if ((!entry.hasParameter(fromParameter)) ||
                                (!entry.hasParameter(toParameter))) {
                            LOG.warn("Missing range limit parameters {} and/or {}",
                                    fromParameter, toParameter);
                        }
                    }
            ).peek(ElasticSearchQueryBasedRangedGroup.this::clearMetaParams));

            return getSubQuery().apply(verifyingData.with(rangeContaining(comparedValue)));
        }

        @Override
        public EntryModification getEntryModification(T value, ElasticSearchData data) {
            return getSubQuery().getEntryModification(value, data.with(rangeContaining(this.comparedValue)));
        }
    }


    public static String getFromParameter(String parameterName) {
        return parameterName + "_range_from";
    }

    public static String getToParameter(String parameterName) {
        return parameterName + "_range_to";
    }

}
