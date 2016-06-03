package be.kwakeroni.parameters.backend.inmemory.basic;

import be.kwakeroni.parameters.backend.inmemory.api.DataQuery;
import be.kwakeroni.parameters.backend.inmemory.api.EntryData;
import be.kwakeroni.parameters.backend.inmemory.api.type.ParameterizedParameterType;
import be.kwakeroni.parameters.backend.inmemory.api.type.Range;
import be.kwakeroni.parameters.basic.connector.RangedEntrySet;
import be.kwakeroni.parameters.client.connector.EntrySet;

import java.util.function.Predicate;

/**
 * (C) 2016 Maarten Van Puymbroeck
 */
public class InMemoryRangedEntrySet<T> implements RangedEntrySet<DataQuery<?>> {

    private final String rangeParameterName;
    private final ParameterizedParameterType<Range<T>> rangeType;
    private final EntrySet<DataQuery<?>> subEntrySet;

    public InMemoryRangedEntrySet(String rangeParameterName, ParameterizedParameterType<Range<T>> rangeType, EntrySet<DataQuery<?>> subEntrySet) {
        this.rangeParameterName = rangeParameterName;
        this.rangeType = rangeType;
        this.subEntrySet = subEntrySet;
    }

    @Override
    public DataQuery<?> getEntryQuery(String value, DataQuery<?> subQuery) {
        return getEntryQuery0(value, subQuery);
    }

    private <Q> DataQuery<Q> getEntryQuery0(String value, DataQuery<Q> subQuery) {
        return stream -> subQuery.apply(stream.filter(entryWithRangeContaining(value)));
    }

    private Predicate<EntryData> entryWithRangeContaining(String value) {
        return entry -> {
            String rangeValue = entry.getValue(rangeParameterName);
            Range<T> range = rangeType.convertFromString(rangeValue);
            return range.contains(rangeType.<T>getTypeArgument(0).convertFromString(value));
        };
    }

    @Override
    public EntrySet<DataQuery<?>> getSubEntrySet() {
        return this.subEntrySet;
    }

}
