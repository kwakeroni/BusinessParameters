package be.kwakeroni.parameters.backend.inmemory.api;

import be.kwakeroni.parameters.backend.api.query.BackendWireFormatterContext;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Stream;

/**
 * (C) 2016 Maarten Van Puymbroeck
 */
public interface InMemoryQuery<T> {

    Optional<T> apply(Stream<EntryData> stream);

    EntryModification getEntryModification(T value, Stream<EntryData> stream);

    Object externalizeResult(T result, BackendWireFormatterContext<? super InMemoryQuery<?>> context);

    T internalizeValue(Object value, BackendWireFormatterContext<? super InMemoryQuery<?>> context);
}
