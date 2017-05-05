package be.kwakeroni.parameters.basic.definition.es;

import be.kwakeroni.parameters.backend.es.api.ElasticSearchGroup;
import be.kwakeroni.parameters.basic.backend.es.ElasticSearchMappedGroup;
import be.kwakeroni.parameters.basic.definition.factory.MappedDefinitionVisitor;

/**
 * Created by kwakeroni on 24/04/17.
 */
public class ElasticSearchMappedGroupFactory implements MappedDefinitionVisitor<ElasticSearchGroup> {

    @Override
    public ElasticSearchGroup visit(Definition definition, ElasticSearchGroup subGroup) {
        return new ElasticSearchMappedGroup(definition.getKeyParameter(), subGroup);
    }
}
