package be.kwakeroni.parameters.backend.es.api;

import be.kwakeroni.parameters.definition.api.DefinitionVisitor;

/**
 * Created by kwakeroni on 05/05/17.
 */
public interface ElasticSearchGroupFactory extends DefinitionVisitor<ElasticSearchGroup> {

    @SuppressWarnings("rawtypes")
    public Class<? extends DefinitionVisitor> getProvidedInterface();

}
