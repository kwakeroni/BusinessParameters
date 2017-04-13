package be.kwakeroni.parameters.definition.api;

/**
 * Created by kwakeroni on 07.04.17.
 */
public interface ParameterGroupDefinition {

    public String getName();

    public <G> G createGroup(GroupBuilderFactoryContext<G> context);

}
