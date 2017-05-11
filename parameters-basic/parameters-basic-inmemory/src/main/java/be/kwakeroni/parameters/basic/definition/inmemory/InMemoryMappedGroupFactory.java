package be.kwakeroni.parameters.basic.definition.inmemory;

import be.kwakeroni.parameters.backend.inmemory.api.InMemoryGroup;
import be.kwakeroni.parameters.backend.inmemory.api.InMemoryGroupFactory;
import be.kwakeroni.parameters.basic.backend.inmemory.InmemoryMappedGroup;
import be.kwakeroni.parameters.basic.definition.factory.MappedDefinitionVisitor;
import be.kwakeroni.parameters.definition.api.DefinitionVisitor;

/**
 * Created by kwakeroni on 14.04.17.
 */
public class InMemoryMappedGroupFactory implements MappedDefinitionVisitor<InMemoryGroup>, InMemoryGroupFactory {
    @Override
    @SuppressWarnings("rawtypes")
    public Class<? extends DefinitionVisitor> getProvidedInterface() {
        return MappedDefinitionVisitor.class;
    }

    @Override
    public InMemoryGroup visit(Definition definition, InMemoryGroup subGroup) {
        return new InmemoryMappedGroup(definition.getKeyParameter(), definition.getDefinition(), subGroup);
    }
}
