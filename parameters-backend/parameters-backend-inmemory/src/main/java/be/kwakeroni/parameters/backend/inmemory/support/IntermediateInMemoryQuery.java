package be.kwakeroni.parameters.backend.inmemory.support;

import be.kwakeroni.parameters.backend.api.query.BackendWireFormatterContext;
import be.kwakeroni.parameters.backend.inmemory.api.EntryData;
import be.kwakeroni.parameters.backend.inmemory.api.EntryModification;
import be.kwakeroni.parameters.backend.inmemory.api.InMemoryQuery;

import java.util.Optional;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

/**
 * (C) 2016 Maarten Van Puymbroeck
 */
public class IntermediateInMemoryQuery<T> implements InMemoryQuery<T> {

    private final InMemoryQuery<T> subQuery;
    private final UnaryOperator<Stream<EntryData>> operator;

    public IntermediateInMemoryQuery(UnaryOperator<Stream<EntryData>> operator, InMemoryQuery<T> subQuery) {
        this.operator = operator;
        this.subQuery = subQuery;
    }

    @Override
    public Optional<T> apply(Stream<EntryData> stream) {
        return subQuery.apply(operator.apply(stream));
    }

    @Override
    public EntryModification getEntryModification(T value, Stream<EntryData> stream) {
        return subQuery.getEntryModification(value, operator.apply(stream));
    }

    @Override
    public T internalizeValue(Object value, BackendWireFormatterContext context) {
        return subQuery.internalizeValue(value, context);
    }

    @Override
    public Object externalizeValue(T value, BackendWireFormatterContext wireFormatterContext) {
        return subQuery.externalizeValue(value, wireFormatterContext);
    }

    public static <T> IntermediateInMemoryQuery<T> filter(Predicate<? super EntryData> filter, InMemoryQuery<T> subQuery) {
        return new IntermediateInMemoryQuery<>(data -> data.filter(filter), subQuery);
    }

}
