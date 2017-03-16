package be.kwakeroni.scratch.tv;

import be.kwakeroni.parameters.backend.api.BackendGroup;
import be.kwakeroni.parameters.backend.inmemory.api.EntryData;
import be.kwakeroni.parameters.backend.inmemory.api.GroupData;
import be.kwakeroni.parameters.backend.inmemory.api.InMemoryQuery;
import be.kwakeroni.parameters.backend.inmemory.support.DefaultEntryData;
import be.kwakeroni.parameters.backend.inmemory.support.FilteredGroupData;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;

/**
 * (C) 2017 Maarten Van Puymbroeck
 */
class DefaultGroupData implements GroupData {

    private final BackendGroup<InMemoryQuery<?>, GroupData, EntryData> group;
    private final List<EntryData> data;

    DefaultGroupData(BackendGroup<InMemoryQuery<?>, GroupData, EntryData> group, EntryData... data) {
        this.group = group;
        this.data = new java.util.ArrayList<>(Arrays.asList(data));
    }

    @Override
    public Stream<EntryData> getEntries() {
        return this.data.stream();
    }

    @Override
    public BackendGroup<InMemoryQuery<?>, GroupData, EntryData> getGroup() {
        return group;
    }

    @Override
    public void addEntry(EntryData data) {
        data = group.prepareAndValidateNewEntry(data, this);
        this.data.add(data);
    }

    @Override
    public void modifyEntry(EntryData data, Consumer<EntryData> modifier) {
        EntryData copy = DefaultEntryData.of(data.asMap());
        modifier.accept(copy);
        EntryData modified = group.prepareAndValidateNewEntry(copy, new FilteredGroupData(this, stream -> stream.filter(entry -> entry!=data)));
        modifier.accept(data);
    }
}
