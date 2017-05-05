package be.kwakeroni.parameters.basic.definition;

import be.kwakeroni.parameters.basic.definition.builder.MappedDefinitionBuilder;
import be.kwakeroni.parameters.basic.definition.factory.MappedDefinitionVisitor;
import be.kwakeroni.parameters.definition.api.ParameterGroupDefinition;
import be.kwakeroni.parameters.definition.api.builder.DefinitionBuilder;
import be.kwakeroni.parameters.definition.api.builder.DefinitionBuilderFinalizer;
import be.kwakeroni.parameters.definition.api.DefinitionVisitorContext;

import java.util.function.Function;

/**
 * Created by kwakeroni on 11.04.17.
 */
final class DefaultMappedDefinition implements MappedDefinitionVisitor.Definition, ParameterGroupDefinition {

    private String keyParameter;
    private ParameterGroupDefinition subGroupDefinition;

    private DefaultMappedDefinition() {

    }

    @Override
    public String getName() {
        return subGroupDefinition.getName();
    }

    @Override
    public String getKeyParameter() {
        return keyParameter;
    }

    @Override
    public <G> G apply(DefinitionVisitorContext<G> context) {
        G subGroup = subGroupDefinition.apply(context);
        return MappedDefinitionVisitor.from(context).visit(this, subGroup);
    }

    static Builder builder() {
        return new DefaultMappedDefinition().new Builder();
    }

    private final class Builder implements MappedDefinitionBuilder {
        private DefinitionBuilder subGroup;

        @Override
        public MappedDefinitionBuilder withKeyParameter(String name) {
            keyParameter = name;
            return this;
        }

        @Override
        public MappedDefinitionBuilder mappingTo(DefinitionBuilder subGroup) {
            this.subGroup = subGroup;
            return this;
        }

        @Override
        public ParameterGroupDefinition build(String name, Function<DefinitionBuilderFinalizer, DefinitionBuilderFinalizer> theirFinalizer) {
            subGroupDefinition = subGroup.build(name, myFinalizer().andThen(theirFinalizer));
            return DefaultMappedDefinition.this;
        }

        private Function<DefinitionBuilderFinalizer, DefinitionBuilderFinalizer> myFinalizer() {
            return builder -> builder.prependParameter(keyParameter);
        }
    }
}
