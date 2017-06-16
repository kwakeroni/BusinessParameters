package be.kwakeroni.parameters.basic.definition.inmemory;

import be.kwakeroni.parameters.backend.inmemory.api.InMemoryGroup;
import be.kwakeroni.parameters.backend.inmemory.api.InMemoryGroupFactory;
import be.kwakeroni.parameters.basic.backend.inmemory.InmemoryMappedGroup;
import be.kwakeroni.parameters.basic.definition.factory.MappedDefinitionVisitor;

import java.util.function.Consumer;

/**
 * Created by kwakeroni on 14.04.17.
 */
public class InMemoryMappedGroupFactory implements MappedDefinitionVisitor<InMemoryGroup>, InMemoryGroupFactory {
    @Override
    public void register(Registry registry) {
        registry.register(MappedDefinitionVisitor.class, this);
    }

    @Override
    public void unregister(Consumer<Class<?>> registry) {
        registry.accept(MappedDefinitionVisitor.class);
    }

    @Override
    public InMemoryGroup visit(Definition definition, InMemoryGroup subGroup) {
        return new InmemoryMappedGroup(definition.getKeyParameter(), definition.getDefinition(), subGroup);
    }
}
