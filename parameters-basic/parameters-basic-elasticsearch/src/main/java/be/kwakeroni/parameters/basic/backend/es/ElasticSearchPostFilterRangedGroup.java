package be.kwakeroni.parameters.basic.backend.es;

import be.kwakeroni.parameters.backend.api.BackendGroup;
import be.kwakeroni.parameters.backend.es.api.ElasticSearchCriteria;
import be.kwakeroni.parameters.backend.es.api.ElasticSearchData;
import be.kwakeroni.parameters.backend.es.api.ElasticSearchEntry;
import be.kwakeroni.parameters.backend.es.api.ElasticSearchQuery;
import be.kwakeroni.parameters.backend.es.api.EntryModification;
import be.kwakeroni.parameters.basic.backend.query.RangedBackendGroup;
import be.kwakeroni.parameters.basic.backend.query.support.IntermediaryBackendGroupSupport;
import be.kwakeroni.parameters.basic.backend.query.support.IntermediateBackendQuerySupport;
import be.kwakeroni.parameters.basic.type.Range;
import be.kwakeroni.parameters.types.api.ParameterType;
import org.json.JSONObject;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * (C) 2017 Maarten Van Puymbroeck
 */
public class ElasticSearchPostFilterRangedGroup
        extends IntermediaryBackendGroupSupport<ElasticSearchQuery<?>, ElasticSearchData, ElasticSearchEntry>
        implements RangedBackendGroup<ElasticSearchQuery<?>, ElasticSearchData, ElasticSearchEntry> {

    private final String rangeParameterName;
    private final ParameterType<Range<String>> rangeType;


    public ElasticSearchPostFilterRangedGroup(String rangeParameterName, ParameterType<Range<String>> rangeType, BackendGroup<ElasticSearchQuery<?>, ElasticSearchData, ElasticSearchEntry> subGroup) {
        super(subGroup);
        this.rangeParameterName = rangeParameterName;
        this.rangeType = rangeType;
    }

    @Override
    public ElasticSearchQuery<?> getEntryQuery(String value, ElasticSearchQuery<?> subQuery) {
        return new ElasticSearchPostFilterQuery<>(
                rangeContaining(value), subQuery
        );
    }

    private Function<Stream<ElasticSearchEntry>, Stream<ElasticSearchEntry>> rangeContaining(String value){
        return stream -> stream.filter(entryWithRangeContaining(value));
    }

    private Predicate<ElasticSearchEntry> entryWithRangeContaining(String value) {
//        return entry -> getRange(entry).contains(value);
        return entry -> {
            System.out.println(value + " in " + getRange(entry).contains(value) + "\r\n " + entry);
            return getRange(entry).contains(value);
        };
    }

    private Range<String> getRange(ElasticSearchEntry entry) {
        return this.rangeType.fromString(entry.getParameter(rangeParameterName));
    }

    private Function<Stream<ElasticSearchEntry>, Stream<ElasticSearchEntry>> rangeWithOverlap(Range<String> range){
        return stream -> stream.filter(entryWithRangeOverlap(range));
    }

    private Predicate<ElasticSearchEntry> entryWithRangeOverlap(Range<String> range){
        return entry -> range.overlaps(getRange(entry));
    }

    @Override
    public ElasticSearchEntry prepareAndValidateNewEntry(ElasticSearchEntry entry, ElasticSearchData storage) {
        Range<String> range = getRange(entry);

        try {
            return getSubGroup().prepareAndValidateNewEntry(entry, storage.withFilter(rangeWithOverlap(range)));
        } catch (IllegalStateException exc) {
            throw new IllegalStateException(exc.getMessage() + " with range: " + this.rangeParameterName + "=" + range);
        }
    }

    private static final class ElasticSearchPostFilterQuery<T>
            extends IntermediateBackendQuerySupport<ElasticSearchQuery<T>, T>
            implements ElasticSearchQuery<T> {

        private final Function<Stream<ElasticSearchEntry>, Stream<ElasticSearchEntry>> filter;

        public ElasticSearchPostFilterQuery(Function<Stream<ElasticSearchEntry>, Stream<ElasticSearchEntry>> filter, ElasticSearchQuery<T> subQuery) {
            super(subQuery);
            this.filter = filter;
        }

        @Override
        public Optional<T> apply(ElasticSearchData data) {
            return getSubQuery().apply(data.withFilter(filter));
        }

        @Override
        public EntryModification getEntryModification(T value, ElasticSearchData data) {
            return getSubQuery().getEntryModification(value, data.withFilter(filter));
        }
    }

}
