package be.kwakeroni.parameters.backend.inmemory.support;

import be.kwakeroni.parameters.api.backend.query.BackendWireFormatterContext;
import be.kwakeroni.parameters.backend.inmemory.api.DataQuery;
import be.kwakeroni.parameters.backend.inmemory.api.EntryData;

import java.util.Optional;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

/**
 * (C) 2016 Maarten Van Puymbroeck
 */
public class IntermediateDataQuery<T> implements DataQuery<T> {

    private final DataQuery<T> subQuery;
    private final UnaryOperator<Stream<EntryData>> operator;

    public IntermediateDataQuery(UnaryOperator<Stream<EntryData>> operator, DataQuery<T> subQuery) {
        this.operator = operator;
        this.subQuery = subQuery;
    }

    @Override
    public Optional<T> apply(Stream<EntryData> stream) {
        return subQuery.apply(operator.apply(stream));
    }

    @Override
    public Object externalizeResult(T result, BackendWireFormatterContext<? super DataQuery<?>> context) {
        return subQuery.externalizeResult(result, context);
    }

    public static <T> IntermediateDataQuery<T> filter(Predicate<? super EntryData> filter, DataQuery<T> subQuery) {
        return new IntermediateDataQuery<>(data -> data.filter(filter), subQuery);
    }

}
