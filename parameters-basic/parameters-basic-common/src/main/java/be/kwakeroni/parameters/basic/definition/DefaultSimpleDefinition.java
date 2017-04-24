package be.kwakeroni.parameters.basic.definition;

import be.kwakeroni.parameters.basic.definition.factory.SimpleGroupFactory;
import be.kwakeroni.parameters.definition.api.builder.DefinitionBuilderFinalizer;
import be.kwakeroni.parameters.definition.api.factory.GroupFactoryContext;
import be.kwakeroni.parameters.definition.api.ParameterGroupDefinition;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

/**
 * Created by kwakeroni on 11.04.17.
 */
final class DefaultSimpleDefinition implements SimpleGroupFactory.Definition, ParameterGroupDefinition {

    private final String name;
    private final List<String> parameters = new ArrayList<>();

    private DefaultSimpleDefinition(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public Collection<String> getParameters() {
        return Collections.unmodifiableCollection(parameters);
    }

    @Override
    public <G> G createGroup(GroupFactoryContext<G> context) {
        return SimpleGroupFactory.from(context).createGroup(this);
    }

    static Builder builder(String name) {
        return new DefaultSimpleDefinition(name).new Builder();
    }

    private final class Builder implements SimpleDefinitionBuilder, DefinitionBuilderFinalizer {

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
        public ParameterGroupDefinition build(Function<DefinitionBuilderFinalizer, DefinitionBuilderFinalizer> finalizer) {
            finalizer.apply(this);
            return DefaultSimpleDefinition.this;
        }

    }
}
