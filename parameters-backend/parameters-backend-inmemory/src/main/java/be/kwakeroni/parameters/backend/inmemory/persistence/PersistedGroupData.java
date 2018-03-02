package be.kwakeroni.parameters.backend.inmemory.persistence;

import be.kwakeroni.evelyn.client.ClientTable;
import be.kwakeroni.parameters.backend.api.BackendGroup;
import be.kwakeroni.parameters.backend.inmemory.api.EntryData;
import be.kwakeroni.parameters.backend.inmemory.api.GroupData;
import be.kwakeroni.parameters.backend.inmemory.api.InMemoryGroup;
import be.kwakeroni.parameters.backend.inmemory.api.InMemoryQuery;
import be.kwakeroni.parameters.backend.inmemory.support.FilteredGroupData;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class PersistedGroupData implements GroupData {

    private final InMemoryGroup group;
    private final ClientTable<EntryData> table;


    public PersistedGroupData(InMemoryGroup group, ClientTable<EntryData> table) {
        this.group = Objects.requireNonNull(group, "group");
        this.table = Objects.requireNonNull(table, "table");
    }

    @Override
    public Stream<EntryData> getEntries() {
        return table.findAll();
    }

    @Override
    public void addEntry(EntryData data) {
        data = group.validateNewEntry(data, this);
        table.append("anonymous", GroupTableOperation.ADD.name(), data.getId(), GroupTableOperation.toString(data));
    }

    @Override
    public void modifyEntry(EntryData data, Consumer<EntryData> modifier) {
        group.validateNewEntry(data, new FilteredGroupData(this, stream -> stream.filter(other -> !data.getId().equals(other.getId()))));
        modifier.accept(data);
        table.append("anonymous", GroupTableOperation.REPLACE.name(), data.getId(), GroupTableOperation.toString(data));
    }

    @Override
    public BackendGroup<InMemoryQuery<?>> getGroup() {
        return this.group;
    }

}
