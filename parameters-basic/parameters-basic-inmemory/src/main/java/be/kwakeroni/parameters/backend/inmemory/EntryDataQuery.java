package be.kwakeroni.parameters.backend.inmemory;

import be.kwakeroni.parameters.api.backend.query.InternalizationContext;
import be.kwakeroni.parameters.backend.inmemory.api.DataQuery;
import be.kwakeroni.parameters.backend.inmemory.api.EntryData;
import be.kwakeroni.parameters.basic.backend.query.BasicInternalizer;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import static be.kwakeroni.parameters.backend.inmemory.InmemorySimpleGroup.atMostOne;

/**
 * (C) 2017 Maarten Van Puymbroeck
 */
class EntryDataQuery implements DataQuery<Map<String, String>> {

    static final EntryDataQuery INSTANCE = new EntryDataQuery();

    @Override
    public Optional<Map<String, String>> apply(Stream<EntryData> stream) {
        return stream.reduce(atMostOne())
                .map(EntryData::asMap);
    }

    @Override
    public Object externalizeResult(Map<String, String> result, InternalizationContext<? super DataQuery<?>> context) {
        return context.getInternalizer(BasicInternalizer.class).externalizeEntryResult(result);
    }
}
