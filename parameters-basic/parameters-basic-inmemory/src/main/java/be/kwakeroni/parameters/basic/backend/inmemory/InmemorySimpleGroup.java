package be.kwakeroni.parameters.basic.backend.inmemory;

import be.kwakeroni.parameters.backend.inmemory.api.InMemoryQuery;
import be.kwakeroni.parameters.backend.inmemory.api.EntryData;
import be.kwakeroni.parameters.basic.backend.query.SimpleBackendGroup;

import java.util.Map;
import java.util.function.BinaryOperator;

/**
 * (C) 2016 Maarten Van Puymbroeck
 */
public class InmemorySimpleGroup implements SimpleBackendGroup<InMemoryQuery<?>> {

    @Override
    public InMemoryQuery<Map<String, String>> getEntryQuery() {
        return EntryInMemoryQuery.INSTANCE;
    }

    @Override
    public InMemoryQuery<String> getValueQuery(String parameterName) {
        return new ValueInMemoryQuery(parameterName);
    }

    static BinaryOperator<EntryData> atMostOne() {
        return (e1, e2) -> {
            throw new IllegalStateException(String.format("More than one entry found: %s and %s",
                    e1, e2));
        };
    }

}
