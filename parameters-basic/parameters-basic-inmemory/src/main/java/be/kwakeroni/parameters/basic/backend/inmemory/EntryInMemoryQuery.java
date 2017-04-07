package be.kwakeroni.parameters.basic.backend.inmemory;

import be.kwakeroni.parameters.backend.api.query.BackendWireFormatterContext;
import be.kwakeroni.parameters.backend.inmemory.api.EntryData;
import be.kwakeroni.parameters.backend.inmemory.api.EntryModification;
import be.kwakeroni.parameters.backend.inmemory.api.InMemoryQuery;
import be.kwakeroni.parameters.basic.backend.query.BasicBackendWireFormatter;

import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Stream;

/**
 * (C) 2017 Maarten Van Puymbroeck
 */
class EntryInMemoryQuery implements InMemoryQuery<Map<String, String>> {

    static final EntryInMemoryQuery INSTANCE = new EntryInMemoryQuery();

    private EntryInMemoryQuery(){

    }

    @Override
    public Optional<Map<String, String>> apply(Stream<EntryData> stream) {
        return getEntryFrom(stream)
                .map(EntryData::asMap);
    }

    @Override
    public EntryModification getEntryModification(Map<String, String> value, Stream<EntryData> stream) {
        return getEntryFrom(stream)
                .map(entry -> new EntryModification() {
                    @Override
                    public EntryData getEntry() {
                        return entry;
                    }

                    @Override
                    public Consumer<EntryData> getModifier() {
                        return data -> value.forEach(data::setValue);
                    }
                })
                .orElseThrow(() -> new IllegalArgumentException("Not found entry to change"));
    }

    private Optional<EntryData> getEntryFrom(Stream<EntryData> stream) {
        return stream.reduce(InmemorySimpleGroup.atMostOne());
    }

    @Override
    public Map<String, String> internalizeValue(Object value, BackendWireFormatterContext context) {
        return context.getWireFormatter(BasicBackendWireFormatter.class).wireToBackendEntry(value);
    }

    @Override
    public Object externalizeValue(Map<String, String> value, BackendWireFormatterContext wireFormatterContext) {
        return wireFormatterContext.getWireFormatter(BasicBackendWireFormatter.class).backendEntryToWire(value);
    }
}
