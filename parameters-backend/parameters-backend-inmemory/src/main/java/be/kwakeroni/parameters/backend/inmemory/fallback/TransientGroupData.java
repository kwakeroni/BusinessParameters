package be.kwakeroni.parameters.backend.inmemory.fallback;

import be.kwakeroni.parameters.backend.api.BackendGroup;
import be.kwakeroni.parameters.backend.inmemory.api.EntryData;
import be.kwakeroni.parameters.backend.inmemory.api.GroupData;
import be.kwakeroni.parameters.backend.inmemory.api.InMemoryGroup;
import be.kwakeroni.parameters.backend.inmemory.api.InMemoryQuery;
import be.kwakeroni.parameters.backend.inmemory.support.DefaultEntryData;
import be.kwakeroni.parameters.backend.inmemory.support.FilteredGroupData;

import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;

/**
 * (C) 2017 Maarten Van Puymbroeck
 */
class TransientGroupData implements GroupData {

    private final InMemoryGroup group;
    private final List<EntryData> data;

    public TransientGroupData(InMemoryGroup group, Collection<EntryData> data) {
        this.group = group;
        this.data = new java.util.ArrayList<>(data);
    }

    @Override
    public Stream<EntryData> getEntries() {
        return this.data.stream();
    }

    @Override
    public BackendGroup<InMemoryQuery<?>> getGroup() {
        return group;
    }

    @Override
    public void addEntry(EntryData data) {
        data = group.validateNewEntry(data, this);
        this.data.add(data);
    }

    @Override
    public void modifyEntry(EntryData data, Consumer<EntryData> modifier) {
        EntryData copy = DefaultEntryData.of(data.asMap());
        modifier.accept(copy);
        EntryData modified = group.validateNewEntry(copy, new FilteredGroupData(this, stream -> stream.filter(entry -> entry != data)));
        modifier.accept(data);
    }
}
