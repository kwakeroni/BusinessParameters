package be.kwakeroni.parameters.backend.inmemory;

import be.kwakeroni.parameters.api.backend.query.BackendWireFormatterContext;
import be.kwakeroni.parameters.backend.inmemory.api.DataQuery;
import be.kwakeroni.parameters.backend.inmemory.api.EntryData;
import be.kwakeroni.parameters.basic.backend.query.BasicBackendWireFormatter;

import java.util.Optional;
import java.util.stream.Stream;

import static be.kwakeroni.parameters.backend.inmemory.InmemorySimpleGroup.atMostOne;

/**
 * (C) 2017 Maarten Van Puymbroeck
 */
class ValueDataQuery implements DataQuery<String> {

    private final String parameterName;

    ValueDataQuery(String parameterName) {
        this.parameterName = parameterName;
    }

    @Override
    public Optional<String> apply(Stream<EntryData> stream) {
        return stream.reduce(atMostOne())
                .map(entry -> entry.getValue(this.parameterName));
    }

    @Override
    public Object externalizeResult(String result, BackendWireFormatterContext<? super DataQuery<?>> context) {
        return context.getWireFormatter(BasicBackendWireFormatter.class).externalizeValueResult(result);
    }
}
