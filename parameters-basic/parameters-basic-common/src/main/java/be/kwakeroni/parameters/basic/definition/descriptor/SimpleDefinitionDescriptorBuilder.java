package be.kwakeroni.parameters.basic.definition.descriptor;

import be.kwakeroni.parameters.basic.definition.factory.SimpleDefinitionVisitor;
import be.kwakeroni.parameters.definition.api.descriptor.DefinitionDescriptor;
import be.kwakeroni.parameters.definition.api.descriptor.DefinitionDescriptorBuilder;

import java.util.function.Consumer;

public class SimpleDefinitionDescriptorBuilder implements SimpleDefinitionVisitor<DefinitionDescriptor>, DefinitionDescriptorBuilder {

    @Override
    public DefinitionDescriptor visit(Definition definition) {
        return DefinitionDescriptor.withType(definition.getDefinition().getType());
    }

    @Override
    public void register(Registry registry) {
        registry.register(SimpleDefinitionVisitor.class, this);
    }

    @Override
    public void unregister(Consumer<Class<?>> registry) {
        registry.accept(SimpleDefinitionVisitor.class);
    }
}
