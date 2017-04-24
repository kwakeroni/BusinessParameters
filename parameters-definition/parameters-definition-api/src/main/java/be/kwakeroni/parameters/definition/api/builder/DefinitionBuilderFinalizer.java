package be.kwakeroni.parameters.definition.api.builder;

/**
 * Created by kwakeroni on 13.04.17.
 */
public interface DefinitionBuilderFinalizer {

    public DefinitionBuilderFinalizer prependParameter(String name);

    public DefinitionBuilderFinalizer appendParameter(String name);


}
