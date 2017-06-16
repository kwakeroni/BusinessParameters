package be.kwakeroni.parameters.backend.inmemory.api;

import be.kwakeroni.parameters.definition.api.DefinitionVisitor;

import java.util.function.Consumer;

/**
 * Created by kwakeroni on 08/05/17.
 */
public interface InMemoryGroupFactory extends DefinitionVisitor<InMemoryGroup> {

    public void register(Registry registry);

    public void unregister(Consumer<Class<?>> registry);

    @FunctionalInterface
    public static interface Registry {
        public <I extends InMemoryGroupFactory> void register(Class<? super I> type, I formatter);
    }

}

