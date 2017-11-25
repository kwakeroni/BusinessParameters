package be.kwakeroni.parameters.definition.api;

import be.kwakeroni.parameters.client.api.BusinessParameters;

import java.util.Collection;

/**
 * Created by kwakeroni on 07.04.17.
 */
public interface ParameterGroupDefinition<GroupType> {

    public String getName();

    public Collection<String> getParameters();

    public String getType();

    public <G> G apply(DefinitionVisitorContext<G> context);

    public GroupType createGroup(BusinessParameters businessParameters);

}
