package be.kwakeroni.parameters.backend.inmemory.api;

import be.kwakeroni.parameters.client.connector.EntrySet;

import java.util.stream.Stream;

/**
 * (C) 2016 Maarten Van Puymbroeck
 */
public interface GroupData {

    Stream<EntryData> getEntries();
    EntrySet<DataQuery<?>> getEntrySet();
}
