package be.kwakeroni.parameters.backend.inmemory.service;

import be.kwakeroni.parameters.backend.api.query.BackendWireFormatter;
import be.kwakeroni.parameters.backend.inmemory.api.InMemoryQuery;
import be.kwakeroni.parameters.backend.api.BackendGroup;
import be.kwakeroni.parameters.backend.api.query.BackendWireFormatterContext;

import java.util.*;

/**
 * (C) 2016 Maarten Van Puymbroeck
 */
public class DefaultBackendWireFormatterContext implements BackendWireFormatterContext<InMemoryQuery<?>> {

    private final Map<Class<?>, BackendWireFormatter> formatters = new HashMap<>(2);

    public <I extends BackendWireFormatter> void register(Class<? super I> type, I formatter){
        this.formatters.put(type, formatter);
    }

    @Override
    public InMemoryQuery<?> internalize(BackendGroup<InMemoryQuery<?>> group, Object query) {
        if (this.formatters.isEmpty()){
            throw new IllegalStateException("No formatters registered");
        }

        return formatters
                .values()
                .stream()
                .map(formatter -> formatter.tryInternalize(group, query, this))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Could not internalize query: " + query));
    }

    @Override
    public <I extends BackendWireFormatter> I getWireFormatter(Class<I> type) {
        Object formatter = this.formatters.get(type);
        if (formatter != null) {
            return type.cast(formatter);
        } else {
            throw new IllegalStateException("No formatter of type " + type.getName() + " registered");
        }
    }
}
