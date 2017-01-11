package be.kwakeroni.parameters.basic.backend.inmemory;

import be.kwakeroni.parameters.backend.api.BackendGroup;
import be.kwakeroni.parameters.backend.inmemory.api.InMemoryQuery;
import be.kwakeroni.parameters.backend.inmemory.api.EntryData;
import be.kwakeroni.parameters.backend.inmemory.support.IntermediateInMemoryQuery;
import be.kwakeroni.parameters.basic.backend.query.RangedBackendGroup;

import java.util.function.BiPredicate;
import java.util.function.Predicate;

/**
 * (C) 2017 Maarten Van Puymbroeck
 */
public class InmemoryRangedGroup implements RangedBackendGroup<InMemoryQuery<?>> {

    private final String rangeParameterName;
    private final BiPredicate<String, String> contains;
    private final BackendGroup<InMemoryQuery<?>> subGroup;

    public InmemoryRangedGroup(String rangeParameterName, BiPredicate<String, String> contains, BackendGroup<InMemoryQuery<?>> subGroup) {
        this.rangeParameterName = rangeParameterName;
        this.contains = contains;
        this.subGroup = subGroup;
    }

    @Override
    public InMemoryQuery<?> getEntryQuery(String value, InMemoryQuery<?> subQuery) {
        return IntermediateInMemoryQuery.filter(entryWithRangeContaining(value), subQuery);
    }

    private Predicate<EntryData> entryWithRangeContaining(String value) {
        return entry -> {
            String range = entry.getValue(rangeParameterName);
            return contains.test(range, value);
        };
    }

    @Override
    public BackendGroup<InMemoryQuery<?>> getSubGroup() {
        return this.subGroup;
    }
}
