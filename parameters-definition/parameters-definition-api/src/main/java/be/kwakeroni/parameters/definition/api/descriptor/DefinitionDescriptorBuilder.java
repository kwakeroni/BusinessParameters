package be.kwakeroni.parameters.definition.api.descriptor;

import be.kwakeroni.parameters.definition.api.DefinitionVisitor;

import java.util.function.Consumer;

public interface DefinitionDescriptorBuilder extends DefinitionVisitor<DefinitionDescriptor> {

    public void register(Registry registry);

    public void unregister(Consumer<Class<?>> registry);

    @FunctionalInterface
    public static interface Registry {
        public <I extends DefinitionDescriptorBuilder> void register(Class<? super I> type, I formatter);
    }


}
