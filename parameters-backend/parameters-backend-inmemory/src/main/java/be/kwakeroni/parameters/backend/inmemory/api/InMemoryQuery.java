package be.kwakeroni.parameters.backend.inmemory.api;

import be.kwakeroni.parameters.backend.api.query.BackendQuery;

import java.util.Optional;
import java.util.stream.Stream;

/**
 * (C) 2016 Maarten Van Puymbroeck
 */
public interface InMemoryQuery<T> extends BackendQuery<InMemoryQuery<T>, T> {

    @Override
    default InMemoryQuery<T> raw() {
        return this;
    }

    Optional<T> apply(Stream<EntryData> stream);

    EntryModification getEntryModification(T value, Stream<EntryData> stream);

}
