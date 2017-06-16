package be.kwakeroni.parameters.backend.inmemory.api;

import be.kwakeroni.parameters.backend.api.BackendGroup;

import java.util.function.Consumer;
import java.util.stream.Stream;

/**
 * (C) 2016 Maarten Van Puymbroeck
 */
public interface GroupData {

    Stream<EntryData> getEntries();

    void addEntry(EntryData data);

    void modifyEntry(EntryData data, Consumer<EntryData> modifier);

    BackendGroup<InMemoryQuery<?>> getGroup();

}
