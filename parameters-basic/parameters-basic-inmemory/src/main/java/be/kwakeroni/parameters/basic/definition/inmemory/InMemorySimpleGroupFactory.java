package be.kwakeroni.parameters.basic.definition.inmemory;

import be.kwakeroni.parameters.backend.inmemory.api.InMemoryGroup;
import be.kwakeroni.parameters.backend.inmemory.api.InMemoryGroupFactory;
import be.kwakeroni.parameters.basic.backend.inmemory.InmemorySimpleGroup;
import be.kwakeroni.parameters.basic.definition.factory.SimpleDefinitionVisitor;

import java.util.LinkedHashSet;
import java.util.function.Consumer;

/**
 * Created by kwakeroni on 14.04.17.
 */
public class InMemorySimpleGroupFactory implements SimpleDefinitionVisitor<InMemoryGroup>, InMemoryGroupFactory {

    @Override
    public void register(Registry registry) {
        registry.register(SimpleDefinitionVisitor.class, this);
    }

    @Override
    public void unregister(Consumer<Class<?>> registry) {
        registry.accept(SimpleDefinitionVisitor.class);
    }

    @Override
    public InMemoryGroup visit(Definition definition) {
        return new InmemorySimpleGroup(definition.getName(), definition.getDefinition(), new LinkedHashSet<>(definition.getParameters()));
    }
}
