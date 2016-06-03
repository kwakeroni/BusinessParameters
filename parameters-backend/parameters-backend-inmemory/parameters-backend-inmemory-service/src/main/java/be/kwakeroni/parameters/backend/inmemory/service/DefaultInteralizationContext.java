package be.kwakeroni.parameters.backend.inmemory.service;

import be.kwakeroni.parameters.backend.inmemory.api.DataQuery;
import be.kwakeroni.parameters.client.connector.EntrySet;
import be.kwakeroni.parameters.client.connector.InternalizationContext;
import be.kwakeroni.parameters.client.connector.QueryInternalizer;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;

/**
 * (C) 2016 Maarten Van Puymbroeck
 */
public class DefaultInteralizationContext implements InternalizationContext<DataQuery<?>> {

    private final Collection<QueryInternalizer> internalizers;

    public DefaultInteralizationContext(Iterable<QueryInternalizer> internalizers){
        HashSet<QueryInternalizer> set = new HashSet<>();
        for (QueryInternalizer internalizer : internalizers){
            set.add(internalizer);
        }
        this.internalizers = set;
    }

    public DefaultInteralizationContext(QueryInternalizer... internalizers) {
        this.internalizers = Arrays.asList(internalizers);
    }

    @Override
    public DataQuery<?> internalize(EntrySet<DataQuery<?>> group, Object query) {
        return internalizers
                .stream()
                .map(internalizer -> internalizer.tryInternalize(query, group, this))
                .filter(result -> result != null)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Could not internalize query: " + query));
    }
}
