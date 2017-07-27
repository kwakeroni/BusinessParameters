package be.kwakeroni.parameters.basic.definition.es;

import be.kwakeroni.parameters.backend.es.api.ElasticSearchGroup;
import be.kwakeroni.parameters.backend.es.api.ElasticSearchGroupFactory;
import be.kwakeroni.parameters.basic.backend.es.ElasticSearchSimpleGroup;
import be.kwakeroni.parameters.basic.definition.factory.SimpleDefinitionVisitor;

import java.util.LinkedHashSet;

/**
 * Created by kwakeroni on 24/04/17.
 */
public class ElasticSearchSimpleGroupFactory implements SimpleDefinitionVisitor<ElasticSearchGroup>, ElasticSearchGroupFactory {

    @Override
    public void visit(Registry registry) {
        registry.register(SimpleDefinitionVisitor.class, this);
    }

    @Override
    public ElasticSearchGroup visit(Definition definition) {
        return new ElasticSearchSimpleGroup(definition.getName(), definition.getDefinition(), new LinkedHashSet<>(definition.getParameters()));
    }
}
