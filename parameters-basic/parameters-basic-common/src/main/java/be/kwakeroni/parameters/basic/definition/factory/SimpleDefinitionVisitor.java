package be.kwakeroni.parameters.basic.definition.factory;

import be.kwakeroni.parameters.definition.api.DefinitionVisitor;
import be.kwakeroni.parameters.definition.api.DefinitionVisitorContext;
import be.kwakeroni.parameters.definition.api.ParameterGroupDefinition;

import java.util.Collection;

/**
 * Created by kwakeroni on 14.04.17.
 */
public interface SimpleDefinitionVisitor<G> extends DefinitionVisitor<G> {

    public G visit(Definition definition);

    public static <G> SimpleDefinitionVisitor<G> from(DefinitionVisitorContext<G> context) {
        return (SimpleDefinitionVisitor<G>) context.getVisitor(SimpleDefinitionVisitor.class);
    }

    public interface Definition {
        public ParameterGroupDefinition<?> getDefinition();

        public String getName();

        public Collection<String> getParameters();
    }
}
