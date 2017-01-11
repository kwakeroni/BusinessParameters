package be.kwakeroni.parameters.basic.backend.inmemory;

import be.kwakeroni.parameters.backend.api.BackendGroup;
import be.kwakeroni.parameters.backend.inmemory.api.InMemoryQuery;
import be.kwakeroni.parameters.backend.inmemory.api.EntryData;
import be.kwakeroni.parameters.backend.inmemory.support.IntermediateInMemoryQuery;
import be.kwakeroni.parameters.basic.backend.query.MappedBackendGroup;

import java.util.function.BiPredicate;
import java.util.function.Predicate;

/**
 * (C) 2017 Maarten Van Puymbroeck
 */
public class InmemoryMappedGroup implements MappedBackendGroup<InMemoryQuery<?>> {

    private final String keyParameterName;
    private final BiPredicate<String, String> equalizer;
    private final BackendGroup<InMemoryQuery<?>> subGroup;

    public InmemoryMappedGroup(String keyParameterName, BiPredicate<String, String> equalizer, BackendGroup<InMemoryQuery<?>> subGroup) {
        this.keyParameterName = keyParameterName;
        this.equalizer = equalizer;
        this.subGroup = subGroup;
    }

    @Override
    public InMemoryQuery<?> getEntryQuery(String keyValue, InMemoryQuery<?> subQuery) {
        return IntermediateInMemoryQuery.filter(entryWithKey(keyValue), subQuery);
    }

    private Predicate<EntryData> entryWithKey(String keyValue) {
        return (entry) -> equalizer.test(keyValue, entry.getValue(keyParameterName));
    }

    @Override
    public BackendGroup<InMemoryQuery<?>> getSubGroup() {
        return this.subGroup;
    }

}
