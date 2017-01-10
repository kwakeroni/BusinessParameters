package be.kwakeroni.parameters.backend.inmemory;

import be.kwakeroni.parameters.api.backend.BackendGroup;
import be.kwakeroni.parameters.backend.inmemory.api.DataQuery;
import be.kwakeroni.parameters.backend.inmemory.api.EntryData;
import be.kwakeroni.parameters.backend.inmemory.support.IntermediateDataQuery;
import be.kwakeroni.parameters.basic.backend.query.MappedBackendGroup;

import java.util.function.BiPredicate;
import java.util.function.Predicate;

/**
 * (C) 2017 Maarten Van Puymbroeck
 */
public class InmemoryMappedGroup implements MappedBackendGroup<DataQuery<?>> {

    private final String keyParameterName;
    private final BiPredicate<String, String> equalizer;
    private final BackendGroup<DataQuery<?>> subGroup;

    public InmemoryMappedGroup(String keyParameterName, BiPredicate<String, String> equalizer, BackendGroup<DataQuery<?>> subGroup) {
        this.keyParameterName = keyParameterName;
        this.equalizer = equalizer;
        this.subGroup = subGroup;
    }

    @Override
    public DataQuery<?> getEntryQuery(String keyValue, DataQuery<?> subQuery) {
        return IntermediateDataQuery.filter(entryWithKey(keyValue), subQuery);
    }

    private Predicate<EntryData> entryWithKey(String keyValue) {
        return (entry) -> equalizer.test(keyValue, entry.getValue(keyParameterName));
    }

    @Override
    public BackendGroup<DataQuery<?>> getSubGroup() {
        return this.subGroup;
    }

}
