package be.kwakeroni.parameters.adapter.jmx.api;

import be.kwakeroni.parameters.definition.api.DefinitionVisitor;

import java.util.function.Consumer;

/**
 * Created by kwakeroni on 09/05/17.
 */
public interface JMXGroupMBeanFactory extends DefinitionVisitor<JMXGroupBuilder> {

    public void register(Registry registry);

    public void unregister(Consumer<Class<?>> registry);

    @FunctionalInterface
    public static interface Registry {
        public <I extends JMXGroupMBeanFactory> void register(Class<? super I> type, I formatter);
    }

}
