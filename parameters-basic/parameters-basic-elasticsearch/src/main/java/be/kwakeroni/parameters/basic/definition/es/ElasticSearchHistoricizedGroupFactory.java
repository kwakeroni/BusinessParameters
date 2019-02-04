package be.kwakeroni.parameters.basic.definition.es;

import be.kwakeroni.parameters.backend.es.api.ElasticSearchGroup;
import be.kwakeroni.parameters.backend.es.api.ElasticSearchGroupFactory;
import be.kwakeroni.parameters.basic.backend.es.ElasticSearchHistoricizedGroup;
import be.kwakeroni.parameters.basic.definition.factory.HistoricizedDefinitionVisitor;

public class ElasticSearchHistoricizedGroupFactory implements HistoricizedDefinitionVisitor<ElasticSearchGroup>, ElasticSearchGroupFactory {

    @Override
    public void visit(Registry registry) {
        registry.register(HistoricizedDefinitionVisitor.class, this);
    }

    @Override
    public ElasticSearchGroup visit(Definition definition, ElasticSearchGroup subGroup) {
        return new ElasticSearchHistoricizedGroup(definition.getPeriodParameter(), definition.getDefinition(), subGroup);
    }
}
