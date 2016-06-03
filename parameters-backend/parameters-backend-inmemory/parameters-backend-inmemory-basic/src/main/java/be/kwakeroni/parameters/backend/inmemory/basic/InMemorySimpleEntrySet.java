package be.kwakeroni.parameters.backend.inmemory.basic;

import be.kwakeroni.parameters.backend.inmemory.api.DataQuery;
import be.kwakeroni.parameters.backend.inmemory.api.EntryData;
import be.kwakeroni.parameters.basic.connector.SimpleEntrySet;

import java.util.Map;
import java.util.function.BinaryOperator;

/**
 * (C) 2016 Maarten Van Puymbroeck
 */
public class InMemorySimpleEntrySet implements SimpleEntrySet<DataQuery<?>> {

    @Override
    public DataQuery<Map<String, String>> getEntryQuery() {
        return stream ->
                stream.reduce(atMostOne())
                        .map(EntryData::asMap);
    }

    @Override
    public DataQuery<String> getValueQuery(String parameterName) {
        return stream ->
                stream.reduce(atMostOne())
                        .map(entry -> entry.getValue(parameterName));
    }

    private static BinaryOperator<EntryData> atMostOne() {
        return (e1, e2) -> {
            throw new IllegalStateException(String.format("More than one entry found: %s and %s",
                    e1, e2));
        };
    }

}
