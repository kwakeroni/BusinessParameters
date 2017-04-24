package be.kwakeroni.parameters.definition.api.builder;

import be.kwakeroni.parameters.definition.api.ParameterGroupDefinition;

import java.util.function.Function;

/**
 * (C) 2017 Maarten Van Puymbroeck
 */
public interface DefinitionBuilder {

    public default ParameterGroupDefinition build() {
        return this.build(Function.identity());
    }

    public ParameterGroupDefinition build(Function<DefinitionBuilderFinalizer, DefinitionBuilderFinalizer> finalizer);
}
