package be.kwakeroni.parameters.definition.ext;

import be.kwakeroni.parameters.client.api.model.EntryType;
import be.kwakeroni.parameters.definition.api.ParameterGroupDefinition;

import java.util.function.Function;

/**
 * (C) 2017 Maarten Van Puymbroeck
 */
public interface DefinitionBuilder<GroupType extends EntryType> {

    public default ParameterGroupDefinition<GroupType> build(String name) {
        return this.build(name, Function.identity());
    }

    public default ParameterGroupDefinition<GroupType> build(String name, Function<DefinitionBuilderFinalizer, DefinitionBuilderFinalizer> finalizer) {
        return this.createPartialDefinition(name, finalizer);
    }

    public PartialDefinition<?, GroupType> createPartialDefinition(String name, Function<DefinitionBuilderFinalizer, DefinitionBuilderFinalizer> finalizer);

}
