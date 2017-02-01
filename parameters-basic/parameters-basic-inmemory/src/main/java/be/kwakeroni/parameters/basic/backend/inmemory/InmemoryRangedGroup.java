package be.kwakeroni.parameters.basic.backend.inmemory;

import be.kwakeroni.parameters.backend.api.BackendGroup;
import be.kwakeroni.parameters.backend.inmemory.api.GroupData;
import be.kwakeroni.parameters.backend.inmemory.api.InMemoryQuery;
import be.kwakeroni.parameters.backend.inmemory.api.EntryData;
import be.kwakeroni.parameters.backend.inmemory.support.FilteredGroupData;
import be.kwakeroni.parameters.backend.inmemory.support.IntermediateInMemoryQuery;
import be.kwakeroni.parameters.basic.backend.query.RangedBackendGroup;
import be.kwakeroni.parameters.basic.type.Range;
import be.kwakeroni.parameters.types.api.ParameterType;

import java.util.function.Predicate;

/**
 * (C) 2017 Maarten Van Puymbroeck
 */
public class InmemoryRangedGroup implements RangedBackendGroup<InMemoryQuery<?>, GroupData, EntryData> {

    private final String rangeParameterName;
    private final ParameterType<Range<String>> rangeType;
    private final BackendGroup<InMemoryQuery<?>, GroupData, EntryData> subGroup;

    public InmemoryRangedGroup(String rangeParameterName, ParameterType<Range<String>> rangeType, BackendGroup<InMemoryQuery<?>, GroupData, EntryData> subGroup) {
        this.rangeParameterName = rangeParameterName;
        this.rangeType = rangeType;
        this.subGroup = subGroup;
    }

    @Override
    public InMemoryQuery<?> getEntryQuery(String value, InMemoryQuery<?> subQuery) {
        return IntermediateInMemoryQuery.filter(entryWithRangeContaining(value), subQuery);
    }

    private Predicate<EntryData> entryWithRangeContaining(String value) {
        return entry -> {
            return getRange(entry).contains(value);
        };
    }

    private Range<String> getRange(EntryData entry){
        return this.rangeType.fromString(entry.getValue(rangeParameterName));
    }

    @Override
    public BackendGroup<InMemoryQuery<?>, GroupData, EntryData> getSubGroup() {
        return this.subGroup;
    }

    @Override
    public String getName() {
        return subGroup.getName();
    }

    @Override
    public void validateNewEntry(EntryData entry, GroupData storage) {
        Range<String> range = getRange(entry);

        try {
            this.subGroup.validateNewEntry(entry, new FilteredGroupData(storage, data -> data.filter(entryWithOverlap(range))));
        } catch (IllegalStateException exc){
            throw new IllegalStateException(exc.getMessage() + " with range " + this.rangeParameterName + "=" + range);
        }
    }

    private Predicate<EntryData> entryWithOverlap(Range<String> range){
        return entry -> {
            return range.overlaps(getRange(entry));
        };
    }
}
