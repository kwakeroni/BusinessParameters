package be.kwakeroni.parameters.core.support.client;

import be.kwakeroni.parameters.client.api.factory.ClientWireFormatterFactory;
import be.kwakeroni.parameters.client.api.query.ClientWireFormatter;
import be.kwakeroni.parameters.client.api.query.ClientWireFormatterContext;
import be.kwakeroni.parameters.core.support.registry.DefaultRegistry;

/**
 * Created by kwakeroni on 30/06/17.
 */
public class DefaultClientWireFormatterContext extends DefaultRegistry<ClientWireFormatter> implements ClientWireFormatterContext {

    @Override
    public <F extends ClientWireFormatter> F getWireFormatter(Class<F> type) {
        return get(type).orElseThrow(() -> new IllegalStateException("No wireformatter registered for " + type));
    }

    public void register(ClientWireFormatterFactory registrar) {
        if (registrar != null) {
            registrar.visitInstances(this::registerInstance);
        }
    }

    public void unregister(ClientWireFormatterFactory registrar) {
        if (registrar != null) {
            registrar.visitInstances(this::unregisterInstance);
        }
    }
}
