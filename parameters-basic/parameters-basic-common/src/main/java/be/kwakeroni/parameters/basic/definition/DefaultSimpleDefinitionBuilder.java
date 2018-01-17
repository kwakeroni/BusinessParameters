package be.kwakeroni.parameters.basic.definition;

import be.kwakeroni.parameters.basic.client.model.Simple;
import be.kwakeroni.parameters.basic.definition.builder.SimpleDefinitionBuilder;
import be.kwakeroni.parameters.definition.ext.DefinitionBuilderFinalizer;
import be.kwakeroni.parameters.definition.ext.PartialDefinition;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

final /* value */ class DefaultSimpleDefinitionBuilder implements SimpleDefinitionBuilder, DefinitionBuilderFinalizer {
    private final List<String> parameters = new ArrayList<>();

    @Override
    public SimpleDefinitionBuilder withParameter(String name) {
        appendParameter(name);
        return this;
    }

    @Override
    public DefinitionBuilderFinalizer prependParameter(String name) {
        parameters.add(0, name);
        return this;
    }

    @Override
    public DefinitionBuilderFinalizer appendParameter(String name) {
        parameters.add(name);
        return this;
    }

    @Override
    public PartialDefinition<?, Simple> createPartialDefinition(String name, Function<DefinitionBuilderFinalizer, DefinitionBuilderFinalizer> finalizer) {
        finalizer.apply(this);
        return new DefaultSimpleDefinition<>(name, parameters);
    }
}
