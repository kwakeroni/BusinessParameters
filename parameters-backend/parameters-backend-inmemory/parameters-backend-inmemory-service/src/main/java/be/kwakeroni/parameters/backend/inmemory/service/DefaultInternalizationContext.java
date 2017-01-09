package be.kwakeroni.parameters.backend.inmemory.service;

import be.kwakeroni.parameters.backend.inmemory.api.DataQuery;
import be.kwakeroni.parameters.api.backend.BackendGroup;
import be.kwakeroni.parameters.api.backend.query.InternalizationContext;
import be.kwakeroni.parameters.api.backend.query.Internalizer;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;

/**
 * (C) 2016 Maarten Van Puymbroeck
 */
public class DefaultInternalizationContext implements InternalizationContext<DataQuery<?>> {

    private final Collection<Internalizer> internalizers;

    public DefaultInternalizationContext(Iterable<Internalizer> internalizers){
        HashSet<Internalizer> set = new HashSet<>();
        for (Internalizer internalizer : internalizers){
            set.add(internalizer);
        }
        this.internalizers = set;
    }

    public DefaultInternalizationContext(Internalizer... internalizers) {
        this.internalizers = Arrays.asList(internalizers);
    }

    public DefaultInternalizationContext(){
        this.internalizers = new HashSet<>();
    }

    public void registerInternalizer(Internalizer internalizer){
        this.internalizers.add(internalizer);
    }

    @Override
    public DataQuery<?> internalize(BackendGroup<DataQuery<?>> group, Object query) {
        if (this.internalizers.isEmpty()){
            throw new IllegalStateException("No internalizers registered");
        }

        return internalizers
                .stream()
                .map(internalizer -> internalizer.tryInternalize(group, query, this))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Could not internalize query: " + query));
    }
}
