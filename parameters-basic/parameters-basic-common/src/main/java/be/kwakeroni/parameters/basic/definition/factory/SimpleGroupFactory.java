package be.kwakeroni.parameters.basic.definition.factory;

import be.kwakeroni.parameters.definition.api.ParameterGroupDefinition;
import be.kwakeroni.parameters.definition.api.factory.GroupFactory;
import be.kwakeroni.parameters.definition.api.factory.GroupFactoryContext;

import java.util.Collection;

/**
 * Created by kwakeroni on 14.04.17.
 */
public interface SimpleGroupFactory<G> extends GroupFactory<G> {

    public G createGroup(Definition definition);

    public static <G> SimpleGroupFactory<G> from(GroupFactoryContext<G> context) {
        return (SimpleGroupFactory<G>) context.getFactory(SimpleGroupFactory.class);
    }

    public interface Definition {
        public String getName();

        public ParameterGroupDefinition getDefinition();

        public Collection<String> getParameters();
    }
}
