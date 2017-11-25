package be.kwakeroni.parameters.definition.ext;

/**
 * Created by kwakeroni on 13.04.17.
 */
public interface DefinitionBuilderFinalizer {

    public DefinitionBuilderFinalizer prependParameter(String name);

    public DefinitionBuilderFinalizer appendParameter(String name);

}
