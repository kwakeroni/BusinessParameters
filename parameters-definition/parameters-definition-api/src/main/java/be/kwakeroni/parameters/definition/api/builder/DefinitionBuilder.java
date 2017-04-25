package be.kwakeroni.parameters.definition.api.builder;

import be.kwakeroni.parameters.definition.api.ParameterGroupDefinition;

import java.util.function.Function;

/**
 * (C) 2017 Maarten Van Puymbroeck
 */
public interface DefinitionBuilder {

    public default ParameterGroupDefinition build(String name) {
        return this.build(name, Function.identity());
    }

    public ParameterGroupDefinition build(String name, Function<DefinitionBuilderFinalizer, DefinitionBuilderFinalizer> finalizer);
}
