package be.kwakeroni.parameters.basic.definition.es;

import be.kwakeroni.parameters.backend.es.api.ElasticSearchGroup;
import be.kwakeroni.parameters.backend.es.api.ElasticSearchGroupFactory;
import be.kwakeroni.parameters.basic.backend.es.ElasticSearchSimpleGroup;
import be.kwakeroni.parameters.basic.definition.factory.SimpleDefinitionVisitor;
import be.kwakeroni.parameters.definition.api.DefinitionVisitor;

import java.util.LinkedHashSet;

/**
 * Created by kwakeroni on 24/04/17.
 */
public class ElasticSearchSimpleGroupFactory implements SimpleDefinitionVisitor<ElasticSearchGroup>, ElasticSearchGroupFactory {

    @Override
    @SuppressWarnings("rawtypes")
    public Class<? extends DefinitionVisitor> getProvidedInterface() {
        return SimpleDefinitionVisitor.class;
    }

    @Override
    public ElasticSearchGroup visit(Definition definition) {
        return new ElasticSearchSimpleGroup(definition.getName(), definition.getDefinition(), new LinkedHashSet<>(definition.getParameters()));
    }
}
