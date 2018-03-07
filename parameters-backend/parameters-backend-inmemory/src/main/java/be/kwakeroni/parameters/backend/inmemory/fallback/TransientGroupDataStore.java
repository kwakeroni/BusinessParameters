package be.kwakeroni.parameters.backend.inmemory.fallback;

import be.kwakeroni.parameters.backend.inmemory.api.EntryData;
import be.kwakeroni.parameters.backend.inmemory.api.GroupData;
import be.kwakeroni.parameters.backend.inmemory.api.InMemoryGroup;
import be.kwakeroni.parameters.backend.inmemory.service.GroupDataStore;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class TransientGroupDataStore implements GroupDataStore {

    private Map<String, Collection<EntryData>> entries = new HashMap<>();

    public void setEntries(String groupName, Collection<EntryData> entries) {
        this.entries.put(groupName, entries);
    }

    @Override
    public GroupData getGroupData(InMemoryGroup group) {
        Collection<EntryData> data = this.entries.computeIfAbsent(group.getName(), __ -> new ArrayList<>(group.initialData()));
        return new TransientGroupData(group, data);
    }
}
