package be.kwakeroni.parameters.backend.inmemory.support;

import be.kwakeroni.parameters.backend.inmemory.api.DataQuery;
import be.kwakeroni.parameters.backend.inmemory.api.EntryData;

import java.util.Optional;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

/**
 * (C) 2016 Maarten Van Puymbroeck
 */
public class IntermediateDataQuery<T> implements DataQuery<T> {

    private final DataQuery<? extends T> subQuery;
    private final UnaryOperator<Stream<EntryData>> operator;

    public IntermediateDataQuery(UnaryOperator<Stream<EntryData>> operator, DataQuery<? extends T> subQuery) {
        this.operator = operator;
        this.subQuery = subQuery;
    }

    @Override
    public Optional<T> apply(Stream<EntryData> stream) {
        return (Optional<T>) subQuery.apply(operator.apply(stream));
    }

}
