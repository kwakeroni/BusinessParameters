package be.kwakeroni.parameters.basic.definition.factory;

import be.kwakeroni.parameters.definition.api.ParameterGroupDefinition;
import be.kwakeroni.parameters.definition.api.factory.GroupFactory;
import be.kwakeroni.parameters.definition.api.factory.GroupFactoryContext;

/**
 * Created by kwakeroni on 14.04.17.
 */
public interface MappedGroupFactory<G> extends GroupFactory<G> {

    public G createGroup(MappedGroupFactory.Definition definition, G subGroup);

    public static <G> MappedGroupFactory<G> from(GroupFactoryContext<G> context) {
        return (MappedGroupFactory<G>) context.getFactory(MappedGroupFactory.class);
    }

    public interface Definition {
        public ParameterGroupDefinition getDefinition();
        public String getKeyParameter();
    }

}
