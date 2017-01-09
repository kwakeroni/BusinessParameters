package be.kwakeroni.parameters.backend.inmemory.service;

import be.kwakeroni.parameters.backend.inmemory.api.DataQuery;
import be.kwakeroni.parameters.api.backend.BackendGroup;
import be.kwakeroni.parameters.api.backend.query.InternalizationContext;
import be.kwakeroni.parameters.api.backend.query.Internalizer;

import java.util.*;

/**
 * (C) 2016 Maarten Van Puymbroeck
 */
public class DefaultInternalizationContext implements InternalizationContext<DataQuery<?>> {

    private final Map<Class<?>, Internalizer> internalizers = new HashMap<>(2);

    public <I extends Internalizer> void register(Class<? super I> type, I externalizer){
        this.internalizers.put(type, externalizer);
    }

    @Override
    public DataQuery<?> internalize(BackendGroup<DataQuery<?>> group, Object query) {
        if (this.internalizers.isEmpty()){
            throw new IllegalStateException("No internalizers registered");
        }

        return internalizers
                .values()
                .stream()
                .map(internalizer -> internalizer.tryInternalize(group, query, this))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Could not internalize query: " + query));
    }

    @Override
    public <I extends Internalizer> I getInternalizer(Class<I> type) {
        Object externalizer = this.internalizers.get(type);
        if (externalizer != null) {
            return type.cast(externalizer);
        } else {
            throw new IllegalStateException("No externalizer of type " + type.getName() + " registered");
        }
    }
}
