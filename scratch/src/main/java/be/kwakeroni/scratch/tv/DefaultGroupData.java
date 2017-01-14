package be.kwakeroni.scratch.tv;

import be.kwakeroni.parameters.backend.api.BackendGroup;
import be.kwakeroni.parameters.backend.inmemory.api.EntryData;
import be.kwakeroni.parameters.backend.inmemory.api.GroupData;
import be.kwakeroni.parameters.backend.inmemory.api.InMemoryQuery;

import java.util.Arrays;
import java.util.stream.Stream;

/**
 * (C) 2017 Maarten Van Puymbroeck
 */
class DefaultGroupData implements GroupData {

    private final BackendGroup<InMemoryQuery<?>> group;
    private final EntryData[] data;

    DefaultGroupData(BackendGroup<InMemoryQuery<?>> group, EntryData... data) {
        this.group = group;
        this.data = data;
    }

    @Override
    public Stream<EntryData> getEntries() {
        return Arrays.stream(data);
    }

    @Override
    public BackendGroup<InMemoryQuery<?>> getGroup() {
        return group;
    }
}
