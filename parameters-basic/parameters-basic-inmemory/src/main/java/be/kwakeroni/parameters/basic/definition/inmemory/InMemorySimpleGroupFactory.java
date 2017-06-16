package be.kwakeroni.parameters.basic.definition.inmemory;

import be.kwakeroni.parameters.backend.inmemory.api.InMemoryGroup;
import be.kwakeroni.parameters.backend.inmemory.api.InMemoryGroupFactory;
import be.kwakeroni.parameters.basic.backend.inmemory.InmemorySimpleGroup;
import be.kwakeroni.parameters.basic.definition.factory.SimpleDefinitionVisitor;
import be.kwakeroni.parameters.definition.api.DefinitionVisitor;

import java.util.LinkedHashSet;

/**
 * Created by kwakeroni on 14.04.17.
 */
public class InMemorySimpleGroupFactory implements SimpleDefinitionVisitor<InMemoryGroup>, InMemoryGroupFactory {

    @Override
    @SuppressWarnings("rawtypes")
    public Class<? extends DefinitionVisitor> getProvidedInterface() {
        return SimpleDefinitionVisitor.class;
    }

    @Override
    public InMemoryGroup visit(Definition definition) {
        return new InmemorySimpleGroup(definition.getName(), new LinkedHashSet<>(definition.getParameters()));
    }
}
