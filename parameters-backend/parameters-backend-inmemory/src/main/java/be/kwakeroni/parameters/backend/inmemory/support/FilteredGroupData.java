package be.kwakeroni.parameters.backend.inmemory.support;

import be.kwakeroni.parameters.backend.api.BackendGroup;
import be.kwakeroni.parameters.backend.inmemory.api.EntryData;
import be.kwakeroni.parameters.backend.inmemory.api.GroupData;
import be.kwakeroni.parameters.backend.inmemory.api.InMemoryQuery;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * (C) 2017 Maarten Van Puymbroeck
 */
public class FilteredGroupData implements GroupData {

    private final GroupData source;
    private final Function<Stream<EntryData>, Stream<EntryData>> filter;


    public FilteredGroupData(GroupData source, Function<Stream<EntryData>, Stream<EntryData>> filter) {
        this.source = source;
        this.filter = filter;
    }

    @Override
    public void addEntry(EntryData data) {
        source.addEntry(data);
    }

    @Override
    public void modifyEntry(EntryData data, Consumer<EntryData> modifier) {
        source.modifyEntry(data, modifier);
    }

    @Override
    public Stream<EntryData> getEntries() {
        return filter.apply(source.getEntries());
    }

    @Override
    public BackendGroup<InMemoryQuery<?>> getGroup() {
        return source.getGroup();
    }
}
