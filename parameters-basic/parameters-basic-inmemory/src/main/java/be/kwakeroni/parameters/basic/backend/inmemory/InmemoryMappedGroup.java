package be.kwakeroni.parameters.basic.backend.inmemory;

import be.kwakeroni.parameters.backend.api.BackendGroup;
import be.kwakeroni.parameters.backend.api.query.BackendQuery;
import be.kwakeroni.parameters.backend.api.query.BackendWireFormatterContext;
import be.kwakeroni.parameters.backend.inmemory.api.EntryData;
import be.kwakeroni.parameters.backend.inmemory.api.GroupData;
import be.kwakeroni.parameters.backend.inmemory.api.InMemoryQuery;
import be.kwakeroni.parameters.backend.inmemory.support.FilteredGroupData;
import be.kwakeroni.parameters.backend.inmemory.support.IntermediateInMemoryQuery;
import be.kwakeroni.parameters.basic.backend.query.MappedBackendGroup;

import java.util.function.BiPredicate;
import java.util.function.Predicate;

/**
 * (C) 2017 Maarten Van Puymbroeck
 */
public class InmemoryMappedGroup implements MappedBackendGroup<InMemoryQuery<?>, GroupData, EntryData> {

    private final String keyParameterName;
    private final BiPredicate<String, String> equalizer;
    private final BackendGroup<InMemoryQuery<?>, GroupData, EntryData> subGroup;

    public InmemoryMappedGroup(String keyParameterName, BiPredicate<String, String> equalizer, BackendGroup<InMemoryQuery<?>, GroupData, EntryData> subGroup) {
        this.keyParameterName = keyParameterName;
        this.equalizer = equalizer;
        this.subGroup = subGroup;
    }


    @Override
    public BackendQuery<? extends InMemoryQuery<?>, ?> internalize(Object query, BackendWireFormatterContext context) {
        return context.internalize(this, query);
    }

    @Override
    public InMemoryQuery<?> getEntryQuery(String keyValue, InMemoryQuery<?> subQuery) {
        return IntermediateInMemoryQuery.filter(entryWithKey(keyValue), subQuery);
    }

    private Predicate<EntryData> entryWithKey(String keyValue) {
        return (entry) -> equalizer.test(keyValue, entry.getValue(keyParameterName));
    }

    @Override
    public BackendGroup<InMemoryQuery<?>, GroupData, EntryData> getSubGroup() {
        return this.subGroup;
    }

    @Override
    public String getName() {
        return this.subGroup.getName();
    }

    @Override
    public void validateNewEntry(EntryData entry, GroupData storage) {
        String key = entry.getValue(this.keyParameterName);

        try {
            this.subGroup.validateNewEntry(entry, new FilteredGroupData(storage, data -> data.filter(entryWithKey(key))));
        } catch (IllegalStateException exc) {
            throw new IllegalStateException(exc.getMessage() + " with key: " + this.keyParameterName + "=" + key);
        }

    }
}
