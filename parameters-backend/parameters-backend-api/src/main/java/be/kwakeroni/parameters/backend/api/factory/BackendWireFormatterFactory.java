package be.kwakeroni.parameters.backend.api.factory;

import be.kwakeroni.parameters.backend.api.query.BackendWireFormatter;

/**
 * (C) 2016 Maarten Van Puymbroeck
 */
public interface BackendWireFormatterFactory {

    public void registerInstance(Registry registry);

    @FunctionalInterface
    public static interface Registry {
        public <I extends BackendWireFormatter> void register(Class<? super I> type, I formatter);
    }
}
