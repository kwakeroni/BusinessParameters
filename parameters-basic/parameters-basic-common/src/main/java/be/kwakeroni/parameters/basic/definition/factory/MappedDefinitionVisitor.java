package be.kwakeroni.parameters.basic.definition.factory;

import be.kwakeroni.parameters.definition.api.DefinitionVisitor;
import be.kwakeroni.parameters.definition.api.DefinitionVisitorContext;

/**
 * Created by kwakeroni on 14.04.17.
 */
public interface MappedDefinitionVisitor<G> extends DefinitionVisitor<G> {

    public G visit(MappedDefinitionVisitor.Definition definition, G subGroup);

    public static <G> MappedDefinitionVisitor<G> from(DefinitionVisitorContext<G> context) {
        return (MappedDefinitionVisitor<G>) context.getVisitor(MappedDefinitionVisitor.class);
    }

    public interface Definition {
        public String getKeyParameter();
    }

}
