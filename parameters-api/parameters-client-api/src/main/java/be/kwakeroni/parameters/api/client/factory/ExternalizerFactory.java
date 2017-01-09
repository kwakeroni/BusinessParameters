package be.kwakeroni.parameters.api.client.factory;

import be.kwakeroni.parameters.api.client.query.Externalizer;

import java.util.function.BiConsumer;

/**
 * (C) 2016 Maarten Van Puymbroeck
 */
public interface ExternalizerFactory {

    public void registerInstance(Registry registry);

    @FunctionalInterface
    public static interface Registry {
        public <E extends Externalizer> void register(Class<? super E> type, E externalizer);
    }
}
