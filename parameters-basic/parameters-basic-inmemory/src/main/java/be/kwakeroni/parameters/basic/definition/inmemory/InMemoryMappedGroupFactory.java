package be.kwakeroni.parameters.basic.definition.inmemory;

import be.kwakeroni.parameters.backend.inmemory.api.InMemoryGroup;
import be.kwakeroni.parameters.basic.backend.inmemory.InmemoryMappedGroup;
import be.kwakeroni.parameters.basic.definition.factory.MappedGroupFactory;

/**
 * Created by kwakeroni on 14.04.17.
 */
public class InMemoryMappedGroupFactory implements MappedGroupFactory<InMemoryGroup> {
    @Override
    public InMemoryGroup createGroup(Definition definition, InMemoryGroup subGroup) {
        return new InmemoryMappedGroup(definition.getKeyParameter(), definition.getDefinition(), subGroup);
    }
}
