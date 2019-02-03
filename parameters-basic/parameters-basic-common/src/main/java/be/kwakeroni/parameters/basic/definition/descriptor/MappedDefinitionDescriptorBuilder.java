package be.kwakeroni.parameters.basic.definition.descriptor;

import be.kwakeroni.parameters.basic.definition.factory.MappedDefinitionVisitor;
import be.kwakeroni.parameters.definition.api.descriptor.DefinitionDescriptor;
import be.kwakeroni.parameters.definition.api.descriptor.DefinitionDescriptorBuilder;

import java.util.function.Consumer;

public class MappedDefinitionDescriptorBuilder implements MappedDefinitionVisitor<DefinitionDescriptor>, DefinitionDescriptorBuilder {

    private static final String KEY_PARAMETER = "keyParameter";

    @Override
    public DefinitionDescriptor visit(Definition definition, DefinitionDescriptor subGroup) {
        return DefinitionDescriptor
                .withType(definition.getDefinition().getType())
                .withSubGroup(subGroup)
                .withProperty(KEY_PARAMETER, definition.getKeyParameter());
    }

    @Override
    public void register(Registry registry) {
        registry.register(MappedDefinitionVisitor.class, this);
    }

    @Override
    public void unregister(Consumer<Class<?>> registry) {
        registry.accept(MappedDefinitionVisitor.class);
    }
}
