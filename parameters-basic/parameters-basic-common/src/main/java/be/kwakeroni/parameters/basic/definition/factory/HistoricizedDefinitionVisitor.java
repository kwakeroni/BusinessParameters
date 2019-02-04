package be.kwakeroni.parameters.basic.definition.factory;

import be.kwakeroni.parameters.definition.api.DefinitionVisitor;
import be.kwakeroni.parameters.definition.api.DefinitionVisitorContext;
import be.kwakeroni.parameters.definition.api.ParameterGroupDefinition;

public interface HistoricizedDefinitionVisitor<G> extends DefinitionVisitor<G> {

    public G visit(HistoricizedDefinitionVisitor.Definition definition, G subGroup);

    public static <G> HistoricizedDefinitionVisitor<G> from(DefinitionVisitorContext<G> context) {
        return (HistoricizedDefinitionVisitor<G>) context.getVisitor(HistoricizedDefinitionVisitor.class);
    }

    public interface Definition {
        public ParameterGroupDefinition<?> getDefinition();

        public String getPeriodParameter();
    }
}
