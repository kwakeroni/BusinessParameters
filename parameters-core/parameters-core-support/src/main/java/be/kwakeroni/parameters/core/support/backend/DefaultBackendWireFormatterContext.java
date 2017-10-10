package be.kwakeroni.parameters.core.support.backend;

import be.kwakeroni.parameters.backend.api.BackendGroup;
import be.kwakeroni.parameters.backend.api.factory.BackendWireFormatterFactory;
import be.kwakeroni.parameters.backend.api.query.BackendWireFormatter;
import be.kwakeroni.parameters.backend.api.query.BackendWireFormatterContext;
import be.kwakeroni.parameters.core.support.registry.DefaultRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Created by kwakeroni on 30/06/17.
 */
public class DefaultBackendWireFormatterContext extends DefaultRegistry<BackendWireFormatter> implements BackendWireFormatterContext {

    Logger LOG = LoggerFactory.getLogger(DefaultBackendWireFormatterContext.class);

    @Override
    public <F extends BackendWireFormatter> F getWireFormatter(Class<F> type) {
        return get(type).orElseThrow(() -> new IllegalStateException("No wireformatter registered for " + type));
    }

    public void register(BackendWireFormatterFactory registrar) {
        registrar.visitInstances(this::registerInstance);
    }

    public void unregister(BackendWireFormatterFactory registrar) {
        registrar.visitInstances(this::unregisterInstance);
    }

    public <Q> Q internalize(BackendGroup<Q> group, Object query) {
        if (this.isEmpty()) {
            throw new IllegalStateException("No formatters registered");
        }

        Optional<Q> result = this.instances()
                .map(formatter -> formatter.tryInternalize(group, query, this))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .findFirst();

        if (!result.isPresent()) {
            LOG.warn("No applicable formatter found for query: {} - available formatters: {}", query,
                    this.instances().map(Object::toString).collect(Collectors.joining(", ", "[", "]")));
        }

        return result
                .orElseThrow(() -> new IllegalStateException("No applicable formatter found for query: " + query));
    }
}
