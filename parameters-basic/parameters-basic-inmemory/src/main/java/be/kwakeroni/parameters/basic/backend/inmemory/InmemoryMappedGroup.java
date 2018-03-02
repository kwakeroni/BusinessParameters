package be.kwakeroni.parameters.basic.backend.inmemory;

import be.kwakeroni.parameters.backend.inmemory.api.EntryData;
import be.kwakeroni.parameters.backend.inmemory.api.GroupData;
import be.kwakeroni.parameters.backend.inmemory.api.InMemoryGroup;
import be.kwakeroni.parameters.backend.inmemory.api.InMemoryQuery;
import be.kwakeroni.parameters.backend.inmemory.support.FilteredGroupData;
import be.kwakeroni.parameters.backend.inmemory.support.IntermediateInMemoryQuery;
import be.kwakeroni.parameters.basic.backend.query.MappedBackendGroup;
import be.kwakeroni.parameters.basic.backend.query.support.IntermediaryBackendGroupSupport;
import be.kwakeroni.parameters.definition.api.ParameterGroupDefinition;

import java.util.function.BiPredicate;
import java.util.function.Predicate;

/**
 * (C) 2017 Maarten Van Puymbroeck
 */
public class InmemoryMappedGroup
        extends IntermediaryBackendGroupSupport<InMemoryQuery<?>, InMemoryGroup>
        implements InMemoryGroup, MappedBackendGroup<InMemoryQuery<?>, InMemoryGroup> {

    private final String keyParameterName;
    private final BiPredicate<String, String> equalizer;

    public InmemoryMappedGroup(String keyParameterName, ParameterGroupDefinition<?> definition, InMemoryGroup subGroup) {
        this(keyParameterName, String::equals, definition, subGroup);
    }

    public InmemoryMappedGroup(String keyParameterName, BiPredicate<String, String> equalizer, ParameterGroupDefinition<?> definition, InMemoryGroup subGroup) {
        super(definition, subGroup);
        this.keyParameterName = keyParameterName;
        this.equalizer = equalizer;
    }

    @Override
    public InMemoryQuery<?> getMappedQuery(String keyValue, InMemoryQuery<?> subQuery) {
        return IntermediateInMemoryQuery.filter(entryWithKey(keyValue), subQuery);
    }

    private Predicate<EntryData> entryWithKey(String keyValue) {
        return (entry) -> equalizer.test(keyValue, entry.getValue(keyParameterName));
    }

    @Override
    public EntryData validateNewEntry(EntryData entry, GroupData storage) {
        String key = entry.getValue(this.keyParameterName);

        try {
            return getSubGroup().validateNewEntry(entry, new FilteredGroupData(storage, data -> data.filter(entryWithKey(key))));
        } catch (IllegalStateException exc) {
            throw new IllegalStateException(exc.getMessage() + " with key: " + this.keyParameterName + "=" + key, exc);
        }

    }

    @Override
    public String toString() {
        return "mapped(" + this.keyParameterName + " : " + getSubGroup() + ")";
    }

}
