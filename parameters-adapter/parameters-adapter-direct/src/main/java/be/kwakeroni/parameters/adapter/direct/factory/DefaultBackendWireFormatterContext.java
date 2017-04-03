package be.kwakeroni.parameters.adapter.direct.factory;

import be.kwakeroni.parameters.backend.api.BackendGroup;
import be.kwakeroni.parameters.backend.api.factory.BackendWireFormatterFactory;
import be.kwakeroni.parameters.backend.api.query.BackendWireFormatter;
import be.kwakeroni.parameters.backend.api.query.BackendWireFormatterContext;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * (C) 2016 Maarten Van Puymbroeck
 */
public class DefaultBackendWireFormatterContext implements BackendWireFormatterContext {

    private final Map<Class<?>, BackendWireFormatter> formatters = new HashMap<>(2);

    public <I extends BackendWireFormatter> void register(Class<? super I> type, I formatter) {
        this.formatters.merge(type, formatter,
                (one, two) -> {
                    throw new IllegalStateException("Duplicate formatters for type: " + type);
                });
    }

    public void unregister(Class<?> type) {
        this.formatters.remove(type);
    }

    public void register(BackendWireFormatterFactory formatterFactory) {
        formatterFactory.registerInstance(this::register);
    }

    public void unregister(BackendWireFormatterFactory formatterFactory) {
        formatterFactory.unregisterInstance(this::unregister);
    }

    @Override
    public <Q> Q internalize(BackendGroup<Q> group, Object query) {
        if (this.formatters.isEmpty()) {
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
