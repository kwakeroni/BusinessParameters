package be.kwakeroni.parameters.basic.backend.inmemory;

import be.kwakeroni.parameters.backend.inmemory.api.EntryData;
import be.kwakeroni.parameters.backend.inmemory.api.GroupData;
import be.kwakeroni.parameters.backend.inmemory.api.InMemoryQuery;
import be.kwakeroni.parameters.basic.backend.query.support.SimpleBackendGroupSupport;

import java.util.Map;
import java.util.Set;

/**
 * (C) 2016 Maarten Van Puymbroeck
 */
public class InmemorySimpleGroup extends SimpleBackendGroupSupport<InMemoryQuery<?>, GroupData, EntryData> {

    public InmemorySimpleGroup(String name, String... parameters) {
        super(name, parameters);
    }

    public InmemorySimpleGroup(String name, Set<String> parameters) {
        super(name, parameters);
    }

    @Override
    public InMemoryQuery<Map<String, String>> getEntryQuery() {
        return EntryInMemoryQuery.INSTANCE;
    }

    @Override
    public InMemoryQuery<String> getValueQuery(String parameterName) {
        return new ValueInMemoryQuery(parameterName);
    }

    @Override
    protected boolean hasAnyEntry(GroupData storage) {
        return storage.getEntries().findAny().isPresent();
    }

    @Override
    protected Map<String, String> asMap(EntryData entry) {
        return entry.asMap();
    }

}
