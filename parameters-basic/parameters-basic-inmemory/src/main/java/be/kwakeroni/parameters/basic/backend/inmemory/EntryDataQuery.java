package be.kwakeroni.parameters.basic.backend.inmemory;

import be.kwakeroni.parameters.backend.api.query.BackendWireFormatterContext;
import be.kwakeroni.parameters.backend.inmemory.api.DataQuery;
import be.kwakeroni.parameters.backend.inmemory.api.EntryData;
import be.kwakeroni.parameters.basic.backend.query.BasicBackendWireFormatter;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * (C) 2017 Maarten Van Puymbroeck
 */
class EntryDataQuery implements DataQuery<Map<String, String>> {

    static final EntryDataQuery INSTANCE = new EntryDataQuery();

    @Override
    public Optional<Map<String, String>> apply(Stream<EntryData> stream) {
        return stream.reduce(InmemorySimpleGroup.atMostOne())
                .map(EntryData::asMap);
    }

    @Override
    public Object externalizeResult(Map<String, String> result, BackendWireFormatterContext<? super DataQuery<?>> context) {
        return context.getWireFormatter(BasicBackendWireFormatter.class).externalizeEntryResult(result);
    }
}
