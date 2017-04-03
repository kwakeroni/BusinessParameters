package be.kwakeroni.parameters.backend.inmemory.service;

import be.kwakeroni.parameters.backend.api.BackendGroup;
import be.kwakeroni.parameters.backend.api.BusinessParametersBackend;
import be.kwakeroni.parameters.backend.api.query.BackendQuery;
import be.kwakeroni.parameters.backend.inmemory.api.EntryData;
import be.kwakeroni.parameters.backend.inmemory.api.EntryModification;
import be.kwakeroni.parameters.backend.inmemory.api.GroupData;
import be.kwakeroni.parameters.backend.inmemory.api.InMemoryQuery;
import be.kwakeroni.parameters.backend.inmemory.support.DefaultEntryData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * (C) 2016 Maarten Van Puymbroeck
 */
public class InMemoryBackend implements BusinessParametersBackend<InMemoryQuery<?>, BackendGroup<InMemoryQuery<?>>> {

    Logger LOG = LoggerFactory.getLogger(InMemoryBackend.class);

    private final Map<String, GroupData> data;

    public InMemoryBackend() {
        this(new HashMap<>());
    }

    private InMemoryBackend(Map<String, GroupData> data) {
        this.data = data;
    }

    public void setGroupData(String groupName, GroupData data) {
        this.data.put(groupName, data);
    }

    public void addGroupData(String groupName, GroupData data) {
        this.data.merge(groupName, data, (key, d) -> {
            throw new IllegalStateException("Duplicate data for group " + key);
        });
    }

    @Override
    public BackendGroup<InMemoryQuery<?>> getGroup(String name) {
        return getGroupData(name).getGroup();
    }

    @Override
    public <V> V select(BackendGroup<InMemoryQuery<?>> group, BackendQuery<? extends InMemoryQuery<?>, V> query) {
        return getValue(query, getGroupData(group.getName()));
    }

    private <T> T getValue(BackendQuery<? extends InMemoryQuery<?>, T> query, GroupData groupData) {
        return (T) query.raw().apply(groupData.getEntries()).orElse(null);
    }

    @Override
    public <V> void update(BackendGroup<InMemoryQuery<?>> group, BackendQuery<? extends InMemoryQuery<?>, V> query, V value) {
        GroupData groupData = getGroupData(group.getName());
        setValue(value, query, groupData);
    }

    private <T> void setValue(T value, BackendQuery<? extends InMemoryQuery<?>, T> query, GroupData groupData) {
        EntryModification modification = ((InMemoryQuery<T>) query.raw()).getEntryModification(value, groupData.getEntries());
        groupData.modifyEntry(modification.getEntry(), modification.getModifier());
    }

    @Override
    public void insert(BackendGroup<InMemoryQuery<?>> group, Map<String, String> entry) {
        GroupData groupData = getGroupData(group.getName());
        EntryData entryData = DefaultEntryData.of(entry);
        groupData.addEntry(entryData);

    }

    private GroupData getGroupData(String name) {
        return Optional.ofNullable(data.get(name))
                .orElseThrow(() -> new IllegalArgumentException("No group defined with name " + name));
    }

    @Override
    public Collection<String> getGroupNames() {
        return data.keySet();
    }
}
