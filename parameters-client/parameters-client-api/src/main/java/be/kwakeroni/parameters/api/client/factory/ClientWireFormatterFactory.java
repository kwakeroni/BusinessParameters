package be.kwakeroni.parameters.api.client.factory;

import be.kwakeroni.parameters.api.client.query.ClientWireFormatter;

/**
 * (C) 2016 Maarten Van Puymbroeck
 */
public interface ClientWireFormatterFactory {

    public void registerInstance(Registry registry);

    @FunctionalInterface
    public static interface Registry {
        public <F extends ClientWireFormatter> void register(Class<? super F> type, F formatter);
    }
}
