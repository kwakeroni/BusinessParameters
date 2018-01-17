package be.kwakeroni.parameters.basic.backend.inmemory;

import be.kwakeroni.parameters.backend.inmemory.api.EntryData;
import be.kwakeroni.parameters.backend.inmemory.api.GroupData;
import be.kwakeroni.parameters.backend.inmemory.api.InMemoryGroup;
import be.kwakeroni.parameters.backend.inmemory.api.InMemoryQuery;
import be.kwakeroni.parameters.backend.inmemory.support.DefaultEntryData;
import be.kwakeroni.parameters.basic.backend.query.support.SimpleBackendGroupSupport;
import be.kwakeroni.parameters.definition.api.ParameterGroupDefinition;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * (C) 2016 Maarten Van Puymbroeck
 */
public class InmemorySimpleGroup extends SimpleBackendGroupSupport<InMemoryQuery<?>, GroupData, EntryData>
        implements InMemoryGroup {

    public InmemorySimpleGroup(String name, ParameterGroupDefinition<?> definition, String... parameters) {
        super(name, definition, parameters);
    }

    public InmemorySimpleGroup(String name, ParameterGroupDefinition<?> definition, Set<String> parameters) {
        super(name, definition, parameters);
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

    @Override
    public EntryData validateNewEntry(EntryData entry, GroupData storage) {
        return super.validateNewEntry(entry, storage);
    }

    @Override
    public String toString() {
        return "simple(InMemory " + getParameterNames() + ")";
    }

    @Override
    public Collection<EntryData> initialData() {
        Map<String, String> entry = new HashMap<>();
        for (String param : getParameterNames()) {
            entry.put(param, "");
        }
        return Collections.singleton(DefaultEntryData.of(entry));
    }
}
