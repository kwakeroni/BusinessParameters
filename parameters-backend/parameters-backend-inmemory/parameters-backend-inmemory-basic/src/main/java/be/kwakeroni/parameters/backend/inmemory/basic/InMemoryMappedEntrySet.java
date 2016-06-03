package be.kwakeroni.parameters.backend.inmemory.basic;

import be.kwakeroni.parameters.backend.inmemory.api.DataQuery;
import be.kwakeroni.parameters.backend.inmemory.api.EntryData;
import be.kwakeroni.parameters.basic.connector.MappedEntrySet;
import be.kwakeroni.parameters.client.connector.EntrySet;

import java.util.function.BiPredicate;
import java.util.function.Predicate;

/**
 * (C) 2016 Maarten Van Puymbroeck
 */
public class InMemoryMappedEntrySet implements MappedEntrySet<DataQuery<?>> {

    private final String keyParameterName;
    private final BiPredicate<String, String> equalizer;
    private final EntrySet<DataQuery<?>> subEntrySet;

    public InMemoryMappedEntrySet(String keyParameterName, BiPredicate<String, String> equalizer, EntrySet<DataQuery<?>> subEntrySet) {
        this.keyParameterName = keyParameterName;
        this.equalizer = equalizer;
        this.subEntrySet = subEntrySet;
    }


    @Override
    public DataQuery<?> getEntryQuery(String keyValue, DataQuery<?> subQuery) {
        return getEntryQuery0(keyValue, subQuery);
    }

    private <T> DataQuery<T> getEntryQuery0(String keyValue, DataQuery<T> subQuery) {
        return data -> subQuery.apply(data.filter(entryWithKey(keyValue)));
    }

    private Predicate<EntryData> entryWithKey(String keyValue) {
        return (entry) -> equalizer.test(keyValue, entry.getValue(keyParameterName));
    }

    @Override
    public EntrySet<DataQuery<?>> getSubEntrySet() {
        return this.subEntrySet;
    }
}
