package be.kwakeroni.parameters.definition.api;

import be.kwakeroni.parameters.definition.api.factory.GroupFactoryContext;

/**
 * Created by kwakeroni on 07.04.17.
 */
public interface ParameterGroupDefinition {

    public <G> G createGroup(GroupFactoryContext<G> context);

}
