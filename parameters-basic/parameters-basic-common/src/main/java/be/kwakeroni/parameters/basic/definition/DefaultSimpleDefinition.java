package be.kwakeroni.parameters.basic.definition;

import be.kwakeroni.parameters.basic.client.model.Simple;
import be.kwakeroni.parameters.basic.definition.builder.SimpleDefinitionBuilder;
import be.kwakeroni.parameters.basic.definition.factory.SimpleDefinitionVisitor;
import be.kwakeroni.parameters.client.api.BusinessParameters;
import be.kwakeroni.parameters.definition.api.DefinitionVisitorContext;
import be.kwakeroni.parameters.definition.api.ParameterGroupDefinition;
import be.kwakeroni.parameters.definition.api.builder.DefinitionBuilderFinalizer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

/**
 * Created by kwakeroni on 11.04.17.
 */
final class DefaultSimpleDefinition implements SimpleDefinitionVisitor.Definition, ParameterGroupDefinition<Simple> {

    private String name;
    private final List<String> parameters = new ArrayList<>();

    private DefaultSimpleDefinition() {
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String getType() {
        return "basic.simple";
    }

    @Override
    public ParameterGroupDefinition<Simple> getDefinition() {
        return this;
    }

    @Override
    public Collection<String> getParameters() {
        return Collections.unmodifiableCollection(parameters);
    }

    @Override
    public <G> G apply(DefinitionVisitorContext<G> context) {
        return SimpleDefinitionVisitor.from(context).visit(this);
    }

    @Override
    public Partial<Simple> createPartial(BusinessParameters businessParameters) {
        return partialQuery -> new DefaultSimpleGroup<>(() -> this.name, businessParameters, partialQuery);
    }

    static Builder builder() {
        return new DefaultSimpleDefinition().new Builder();
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
        public ParameterGroupDefinition<Simple> build(String name, Function<DefinitionBuilderFinalizer, DefinitionBuilderFinalizer> finalizer) {
            DefaultSimpleDefinition.this.name = name;
            finalizer.apply(this);
            return DefaultSimpleDefinition.this;
        }

    }
}
