package be.kwakeroni.parameters.basic.definition.inmemory;

import be.kwakeroni.parameters.backend.inmemory.api.InMemoryGroup;
import be.kwakeroni.parameters.basic.backend.inmemory.InmemoryMappedGroup;
import be.kwakeroni.parameters.basic.definition.factory.MappedDefinitionVisitor;

/**
 * Created by kwakeroni on 14.04.17.
 */
public class InMemoryMappedGroupFactory implements MappedDefinitionVisitor<InMemoryGroup> {
    @Override
    public InMemoryGroup visit(Definition definition, InMemoryGroup subGroup) {
        return new InmemoryMappedGroup(definition.getKeyParameter(), subGroup);
    }
}
