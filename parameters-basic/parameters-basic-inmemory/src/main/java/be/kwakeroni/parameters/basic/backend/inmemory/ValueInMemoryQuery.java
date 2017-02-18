package be.kwakeroni.parameters.basic.backend.inmemory;

import be.kwakeroni.parameters.backend.api.query.BackendWireFormatterContext;
import be.kwakeroni.parameters.backend.inmemory.api.EntryData;
import be.kwakeroni.parameters.backend.inmemory.api.EntryModification;
import be.kwakeroni.parameters.backend.inmemory.api.InMemoryQuery;
import be.kwakeroni.parameters.basic.backend.query.BasicBackendWireFormatter;

import java.util.Optional;
import java.util.function.Consumer;
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
    public EntryModification getEntryModification(String value, Stream<EntryData> stream) {
        return getEntryFrom(stream)
                .map(entry -> new EntryModification() {
                    @Override
                    public EntryData getEntry() {
                        return entry;
                    }

                    @Override
                    public Consumer<EntryData> getModifier() {
                        return entry -> entry.setValue(parameterName, value);
                    }
                })
                .orElseThrow(() -> new IllegalArgumentException("Not found entry to change"));
    }

    private Optional<EntryData> getEntryFrom(Stream<EntryData> stream) {
        return stream.reduce(InmemorySimpleGroup.atMostOne());
    }

    @Override
    public String internalizeValue(Object value, BackendWireFormatterContext context) {
        return context.getWireFormatter(BasicBackendWireFormatter.class).wireToBackendValue(value);
    }

    @Override
    public Object externalizeValue(String value, BackendWireFormatterContext context) {
        return context.getWireFormatter(BasicBackendWireFormatter.class).backendValueToWire(value);
    }
}
