package be.kwakeroni.parameters.backend.inmemory;

import be.kwakeroni.parameters.backend.inmemory.api.DataQuery;
import be.kwakeroni.parameters.backend.inmemory.api.EntryData;
import be.kwakeroni.parameters.basic.backend.query.SimpleBackendGroup;

import java.util.Map;
import java.util.function.BinaryOperator;

/**
 * (C) 2016 Maarten Van Puymbroeck
 */
public class InmemorySimpleGroup implements SimpleBackendGroup<DataQuery<?>> {

    @Override
    public DataQuery<Map<String, String>> getEntryQuery() {
        return EntryDataQuery.INSTANCE;
    }

    @Override
    public DataQuery<String> getValueQuery(String parameterName) {
        return new ValueDataQuery(parameterName);
    }

    static BinaryOperator<EntryData> atMostOne() {
        return (e1, e2) -> {
            throw new IllegalStateException(String.format("More than one entry found: %s and %s",
                    e1, e2));
        };
    }

}
