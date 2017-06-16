package be.kwakeroni.parameters.basic.definition.es;

import be.kwakeroni.parameters.backend.es.api.ElasticSearchGroup;
import be.kwakeroni.parameters.backend.es.api.ElasticSearchGroupFactory;
import be.kwakeroni.parameters.basic.backend.es.ElasticSearchMappedGroup;
import be.kwakeroni.parameters.basic.definition.factory.MappedDefinitionVisitor;
import be.kwakeroni.parameters.definition.api.DefinitionVisitor;

/**
 * Created by kwakeroni on 24/04/17.
 */
public class ElasticSearchMappedGroupFactory implements MappedDefinitionVisitor<ElasticSearchGroup>, ElasticSearchGroupFactory {

    @Override
    @SuppressWarnings("rawtypes")
    public Class<? extends DefinitionVisitor> getProvidedInterface() {
        return MappedDefinitionVisitor.class;
    }

    @Override
    public ElasticSearchGroup visit(Definition definition, ElasticSearchGroup subGroup) {
        return new ElasticSearchMappedGroup(definition.getKeyParameter(), subGroup);
    }
}
