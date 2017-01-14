package be.kwakeroni.parameters.basic.backend.inmemory;

import be.kwakeroni.parameters.backend.api.query.BackendWireFormatterContext;
import be.kwakeroni.parameters.backend.inmemory.api.EntryData;
import be.kwakeroni.parameters.backend.inmemory.api.InMemoryQuery;
import be.kwakeroni.parameters.basic.backend.query.BasicBackendWireFormatter;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * (C) 2017 Maarten Van Puymbroeck
 */
class EntryInMemoryQuery implements InMemoryQuery<Map<String, String>> {

    static final EntryInMemoryQuery INSTANCE = new EntryInMemoryQuery();

    @Override
    public Optional<Map<String, String>> apply(Stream<EntryData> stream) {
        return getEntryFrom(stream)
                .map(EntryData::asMap);
    }

    @Override
    public void setValue(Map<String, String> entry, Stream<EntryData> stream) {
        getEntryFrom(stream).ifPresent(data -> entry.forEach(data::setValue));
    }

    private Optional<EntryData> getEntryFrom(Stream<EntryData> stream) {
        return stream.reduce(InmemorySimpleGroup.atMostOne());
    }

    @Override
    public Object externalizeResult(Map<String, String> result, BackendWireFormatterContext<? super InMemoryQuery<?>> context) {
        return context.getWireFormatter(BasicBackendWireFormatter.class).backendEntryToWire(result);
    }

    @Override
    public Map<String, String> internalizeValue(Object value, BackendWireFormatterContext<? super InMemoryQuery<?>> context) {
        return context.getWireFormatter(BasicBackendWireFormatter.class).wireToBackendEntry(value);
    }
}
