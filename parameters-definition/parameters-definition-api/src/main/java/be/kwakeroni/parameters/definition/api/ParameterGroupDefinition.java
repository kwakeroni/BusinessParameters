package be.kwakeroni.parameters.definition.api;

import java.util.Collection;

/**
 * Created by kwakeroni on 07.04.17.
 */
public interface ParameterGroupDefinition {

    public String getName();

    public Collection<String> getParameters();

    public String getType();

    public <G> G apply(DefinitionVisitorContext<G> context);

}
