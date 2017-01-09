package be.kwakeroni.parameters.api.backend.factory;

import be.kwakeroni.parameters.api.backend.query.Internalizer;

/**
 * (C) 2016 Maarten Van Puymbroeck
 */
public interface InternalizerFactory {

    public void registerInstance(Registry registry);

    @FunctionalInterface
    public static interface Registry {
        public <I extends Internalizer> void register(Class<? super I> type, I internalizer);
    }
}
