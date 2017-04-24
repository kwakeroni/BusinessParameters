package be.kwakeroni.parameters.basic.definition.inmemory;

import be.kwakeroni.parameters.backend.inmemory.api.InMemoryGroup;
import be.kwakeroni.parameters.basic.backend.inmemory.InmemorySimpleGroup;
import be.kwakeroni.parameters.basic.definition.factory.SimpleGroupFactory;

import java.util.LinkedHashSet;

/**
 * Created by kwakeroni on 14.04.17.
 */
public class InMemorySimpleGroupFactory implements SimpleGroupFactory<InMemoryGroup> {
    @Override
    public InMemoryGroup createGroup(Definition definition) {
        return new InmemorySimpleGroup(definition.getName(), new LinkedHashSet<>(definition.getParameters()));
    }
}
