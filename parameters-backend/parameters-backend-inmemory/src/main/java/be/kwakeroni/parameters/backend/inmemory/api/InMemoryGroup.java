package be.kwakeroni.parameters.backend.inmemory.api;

import be.kwakeroni.parameters.backend.api.BackendGroup;

import java.util.Collection;
import java.util.Collections;

/**
 * (C) 2017 Maarten Van Puymbroeck
 */
public interface InMemoryGroup extends BackendGroup<InMemoryQuery<?>> {

    public EntryData validateNewEntry(EntryData entry, GroupData storage);

    public default Collection<EntryData> initialData() {
        return Collections.emptySet();
    }
}
