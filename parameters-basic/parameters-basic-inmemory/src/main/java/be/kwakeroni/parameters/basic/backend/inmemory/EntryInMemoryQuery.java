package be.kwakeroni.parameters.basic.backend.inmemory;

import be.kwakeroni.parameters.backend.api.query.BackendWireFormatterContext;
import be.kwakeroni.parameters.backend.inmemory.api.InMemoryQuery;
import be.kwakeroni.parameters.backend.inmemory.api.EntryData;
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
        return stream.reduce(InmemorySimpleGroup.atMostOne())
                .map(EntryData::asMap);
    }

    @Override
    public Object externalizeResult(Map<String, String> result, BackendWireFormatterContext<? super InMemoryQuery<?>> context) {
        return context.getWireFormatter(BasicBackendWireFormatter.class).externalizeEntryResult(result);
    }
}
