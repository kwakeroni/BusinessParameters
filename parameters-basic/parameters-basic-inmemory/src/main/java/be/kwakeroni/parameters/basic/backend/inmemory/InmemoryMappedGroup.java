package be.kwakeroni.parameters.basic.backend.inmemory;

import be.kwakeroni.parameters.backend.api.BackendGroup;
import be.kwakeroni.parameters.backend.inmemory.api.EntryData;
import be.kwakeroni.parameters.backend.inmemory.api.GroupData;
import be.kwakeroni.parameters.backend.inmemory.api.InMemoryQuery;
import be.kwakeroni.parameters.backend.inmemory.support.FilteredGroupData;
import be.kwakeroni.parameters.backend.inmemory.support.IntermediateInMemoryQuery;
import be.kwakeroni.parameters.basic.backend.query.MappedBackendGroup;
import be.kwakeroni.parameters.basic.backend.query.support.IntermediaryBackendGroupSupport;

import java.util.function.BiPredicate;
import java.util.function.Predicate;

/**
 * (C) 2017 Maarten Van Puymbroeck
 */
public class InmemoryMappedGroup
        extends IntermediaryBackendGroupSupport<InMemoryQuery<?>, GroupData, EntryData>
        implements MappedBackendGroup<InMemoryQuery<?>, GroupData, EntryData> {

    private final String keyParameterName;
    private final BiPredicate<String, String> equalizer;

    public InmemoryMappedGroup(String keyParameterName, BackendGroup<InMemoryQuery<?>, GroupData, EntryData> subGroup) {
        this(keyParameterName, String::equals, subGroup);
    }

    public InmemoryMappedGroup(String keyParameterName, BiPredicate<String, String> equalizer, BackendGroup<InMemoryQuery<?>, GroupData, EntryData> subGroup) {
        super(subGroup);
        this.keyParameterName = keyParameterName;
        this.equalizer = equalizer;
    }

    @Override
    public InMemoryQuery<?> getEntryQuery(String keyValue, InMemoryQuery<?> subQuery) {
        return IntermediateInMemoryQuery.filter(entryWithKey(keyValue), subQuery);
    }

    private Predicate<EntryData> entryWithKey(String keyValue) {
        return (entry) -> equalizer.test(keyValue, entry.getValue(keyParameterName));
    }

    @Override
    public void validateNewEntry(EntryData entry, GroupData storage) {
        String key = entry.getValue(this.keyParameterName);

        try {
            getSubGroup().validateNewEntry(entry, new FilteredGroupData(storage, data -> data.filter(entryWithKey(key))));
        } catch (IllegalStateException exc) {
            throw new IllegalStateException(exc.getMessage() + " with key: " + this.keyParameterName + "=" + key);
        }

    }
}
