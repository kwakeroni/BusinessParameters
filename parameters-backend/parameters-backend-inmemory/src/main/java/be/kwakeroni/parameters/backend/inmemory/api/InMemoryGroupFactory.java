package be.kwakeroni.parameters.backend.inmemory.api;

import be.kwakeroni.parameters.definition.api.DefinitionVisitor;

/**
 * Created by kwakeroni on 08/05/17.
 */
public interface InMemoryGroupFactory extends DefinitionVisitor<InMemoryGroup> {

    @SuppressWarnings("rawtypes")
    public Class<? extends DefinitionVisitor> getProvidedInterface();

}

