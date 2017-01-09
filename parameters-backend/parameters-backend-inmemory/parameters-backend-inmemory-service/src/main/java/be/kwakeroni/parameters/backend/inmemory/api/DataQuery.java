package be.kwakeroni.parameters.backend.inmemory.api;

import be.kwakeroni.parameters.api.backend.query.InternalizationContext;

import java.util.Optional;
import java.util.stream.Stream;

/**
 * (C) 2016 Maarten Van Puymbroeck
 */
public interface DataQuery<T> {

    Optional<T> apply(Stream<EntryData> stream);

    Object externalizeResult(T result, InternalizationContext<? super DataQuery<?>> context);
}
