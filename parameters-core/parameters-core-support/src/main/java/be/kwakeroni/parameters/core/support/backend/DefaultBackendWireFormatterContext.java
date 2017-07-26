package be.kwakeroni.parameters.core.support.backend;

import be.kwakeroni.parameters.backend.api.BackendGroup;
import be.kwakeroni.parameters.backend.api.factory.BackendWireFormatterFactory;
import be.kwakeroni.parameters.backend.api.query.BackendWireFormatter;
import be.kwakeroni.parameters.backend.api.query.BackendWireFormatterContext;
import be.kwakeroni.parameters.core.support.registry.DefaultRegistry;

import java.util.Optional;

/**
 * Created by kwakeroni on 30/06/17.
 */
public class DefaultBackendWireFormatterContext extends DefaultRegistry<BackendWireFormatter> implements BackendWireFormatterContext {

    @Override
    public <F extends BackendWireFormatter> F getWireFormatter(Class<F> type) {
        return get(type).orElseThrow(() -> new IllegalStateException("No wireformatter registered for " + type));
    }

    public void register(BackendWireFormatterFactory registrar) {
        if (registrar != null) {
            registrar.visitInstances(this::registerInstance);
        }
    }

    public void unregister(BackendWireFormatterFactory registrar) {
        if (registrar != null) {
            registrar.visitInstances(this::unregisterInstance);
        }
    }

    public <Q> Q internalize(BackendGroup<Q> group, Object query) {
        if (this.isEmpty()) {
            throw new IllegalStateException("No formatters registered");
        }

        return this.instances()
                .map(formatter -> formatter.tryInternalize(group, query, this))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Could not internalize query: " + query));
    }
}
