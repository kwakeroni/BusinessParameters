package be.kwakeroni.parameters.basic.backend.inmemory;

import be.kwakeroni.parameters.backend.api.query.BackendWireFormatterContext;
import be.kwakeroni.parameters.backend.inmemory.api.InMemoryQuery;
import be.kwakeroni.parameters.backend.inmemory.api.EntryData;
import be.kwakeroni.parameters.basic.backend.query.BasicBackendWireFormatter;

import java.util.Optional;
import java.util.stream.Stream;

/**
 * (C) 2017 Maarten Van Puymbroeck
 */
class ValueInMemoryQuery implements InMemoryQuery<String> {

    private final String parameterName;

    ValueInMemoryQuery(String parameterName) {
        this.parameterName = parameterName;
    }

    @Override
    public Optional<String> apply(Stream<EntryData> stream) {
        return getEntryFrom(stream)
                    .map(entry -> entry.getValue(this.parameterName));
    }


    @Override
    public void setValue(String value, Stream<EntryData> stream) {
        getEntryFrom(stream).ifPresent(entry -> entry.setValue(this.parameterName, value));
    }

    private Optional<EntryData> getEntryFrom(Stream<EntryData> stream){
        return stream.reduce(InmemorySimpleGroup.atMostOne());
    }

    @Override
    public Object externalizeResult(String result, BackendWireFormatterContext<? super InMemoryQuery<?>> context) {
        return context.getWireFormatter(BasicBackendWireFormatter.class).backendValueToWire(result);
    }


    @Override
    public String internalizeValue(Object value, BackendWireFormatterContext<? super InMemoryQuery<?>> context) {
        return context.getWireFormatter(BasicBackendWireFormatter.class).wireToBackendValue(value);
    }
}
