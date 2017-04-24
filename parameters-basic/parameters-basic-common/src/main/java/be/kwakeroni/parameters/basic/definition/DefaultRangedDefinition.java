package be.kwakeroni.parameters.basic.definition;

import be.kwakeroni.parameters.basic.definition.factory.RangedGroupFactory;
import be.kwakeroni.parameters.definition.api.builder.DefinitionBuilder;
import be.kwakeroni.parameters.definition.api.builder.DefinitionBuilderFinalizer;
import be.kwakeroni.parameters.definition.api.factory.GroupFactoryContext;
import be.kwakeroni.parameters.definition.api.ParameterGroupDefinition;
import be.kwakeroni.parameters.types.api.ParameterType;
import be.kwakeroni.parameters.types.support.BasicType;

import java.util.Comparator;
import java.util.function.Function;

/**
 * Created by kwakeroni on 11.04.17.
 */
final class DefaultRangedDefinition implements RangedGroupFactory.Definition, ParameterGroupDefinition {

    private String rangeParameter;
    private ParameterGroupDefinition subGroupDefinition;
    private Factory factory;

    private interface Factory {
        <G> G createGroup(RangedGroupFactory<G> factory, RangedGroupFactory.Definition definition, G subGroup);
    }

    public String getRangeParameter() {
        return rangeParameter;
    }

    @Override
    public <G> G createGroup(GroupFactoryContext<G> context) {
        G subGroup = subGroupDefinition.createGroup(context);
        return factory.createGroup(RangedGroupFactory.from(context), this, subGroup);
    }

    static Builder builder() {
        return new DefaultRangedDefinition().new Builder();
    }

    private final class Builder implements RangedGroupBuilder {
        private DefinitionBuilder subGroup;

        @Override
        public <T extends Comparable<? super T>> RangedGroupBuilder withComparableRangeParameter(String name, ParameterType<T> type) {
            rangeParameter = name;
            factory = new Factory() {
                @Override
                public <G> G createGroup(RangedGroupFactory<G> f, RangedGroupFactory.Definition d, G s) {
                    return f.createGroup(d, type, s);
                }
            };
            return this;
        }

        @Override
        public <T> RangedGroupBuilder withRangeParameter(String name, ParameterType<T> type, Comparator<? super T> comparator) {
            rangeParameter = name;
            factory = new Factory() {
                @Override
                public <G> G createGroup(RangedGroupFactory<G> factory, RangedGroupFactory.Definition definition, G subGroup) {
                    return factory.createGroup(definition, type, comparator, subGroup);
                }
            };
            return this;
        }

        @Override
        public <T, B> RangedGroupBuilder withRangeParameter(String name, BasicType<T, B> type) {
            rangeParameter = name;
            factory = new Factory() {
                @Override
                public <G> G createGroup(RangedGroupFactory<G> factory, RangedGroupFactory.Definition definition, G subGroup) {
                    return factory.createGroup(definition, type, subGroup);
                }
            };
            return this;
        }

        @Override
        public <T, B> RangedGroupBuilder withRangeParameter(String name, ParameterType<T> type, Function<T, B> converter, BasicType<B, B> basicType) {
            rangeParameter = name;
            factory = new Factory() {
                @Override
                public <G> G createGroup(RangedGroupFactory<G> factory, RangedGroupFactory.Definition definition, G subGroup) {
                    return factory.createGroup(definition, type, converter, basicType, subGroup);
                }
            };
            return this;
        }

        @Override
        public RangedGroupBuilder mappingTo(DefinitionBuilder subGroup) {
            this.subGroup = subGroup;
            return this;
        }

        @Override
        public ParameterGroupDefinition build(Function<DefinitionBuilderFinalizer, DefinitionBuilderFinalizer> theirFinalizer) {
            subGroupDefinition = subGroup.build(myFinalizer().andThen(theirFinalizer));
            return DefaultRangedDefinition.this;
        }

        private Function<DefinitionBuilderFinalizer, DefinitionBuilderFinalizer> myFinalizer() {
            return builder -> builder.prependParameter(rangeParameter);
        }

    }
}
