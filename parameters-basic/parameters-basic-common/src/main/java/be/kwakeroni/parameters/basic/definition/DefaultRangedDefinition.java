package be.kwakeroni.parameters.basic.definition;

import be.kwakeroni.parameters.basic.client.model.Ranged;
import be.kwakeroni.parameters.basic.definition.builder.RangedDefinitionBuilder;
import be.kwakeroni.parameters.basic.definition.factory.RangedDefinitionVisitor;
import be.kwakeroni.parameters.client.api.BusinessParameters;
import be.kwakeroni.parameters.client.api.model.EntryType;
import be.kwakeroni.parameters.client.api.query.PartialQuery;
import be.kwakeroni.parameters.definition.api.DefinitionVisitorContext;
import be.kwakeroni.parameters.definition.api.ParameterGroupDefinition;
import be.kwakeroni.parameters.definition.api.builder.DefinitionBuilder;
import be.kwakeroni.parameters.definition.api.builder.DefinitionBuilderFinalizer;
import be.kwakeroni.parameters.types.api.ParameterType;
import be.kwakeroni.parameters.types.support.BasicType;

import java.util.Collection;
import java.util.Comparator;
import java.util.function.Function;

/**
 * Created by kwakeroni on 11.04.17.
 */
final class DefaultRangedDefinition<ValueType, SubType extends EntryType> implements RangedDefinitionVisitor.Definition, ParameterGroupDefinition<Ranged<ValueType, SubType>> {

    private String rangeParameter;
    private ParameterGroupDefinition<SubType> subGroupDefinition;
    private Factory<ValueType, SubType> factory;

    private interface Factory<ValueType, SubType extends EntryType> {
        <G> G createGroup(RangedDefinitionVisitor<G> factory, RangedDefinitionVisitor.Definition definition, G subGroup);

        Ranged<ValueType, SubType> createGroup(Partial<SubType> subDefinition, PartialQuery<?, Ranged<ValueType, SubType>> query);
    }

    @Override
    public String getName() {
        return subGroupDefinition.getName();
    }

    @Override
    public Collection<String> getParameters() {
        return subGroupDefinition.getParameters();
    }

    @Override
    public String getType() {
        return "basic.ranged";
    }

    public String getRangeParameter() {
        return rangeParameter;
    }

    @Override
    public ParameterGroupDefinition<Ranged<ValueType, SubType>> getDefinition() {
        return this;
    }

    @Override
    public Partial<Ranged<ValueType, SubType>> createPartial(BusinessParameters businessParameters) {
        Partial<SubType> subDefinition = subGroupDefinition.createPartial(businessParameters);
        return partialQuery -> factory.createGroup(subDefinition, partialQuery);
    }

    @Override
    public <G> G apply(DefinitionVisitorContext<G> context) {
        G subGroup = subGroupDefinition.apply(context);
        return factory.createGroup(RangedDefinitionVisitor.from(context), this, subGroup);
    }

    static DefaultRangedDefinition<?, ?>.Builder builder() {
        return new DefaultRangedDefinition<>().new Builder();
    }

    private final class Builder implements RangedDefinitionBuilder<ValueType, SubType> {
        private DefinitionBuilder<SubType> subGroup;

        @Override
        public <T extends Comparable<? super T>> RangedDefinitionBuilder<T, SubType> withComparableRangeParameter(String name, ParameterType<T> type) {
            rangeParameter = name;
            factory = (Factory<ValueType, SubType>) new Factory<T, SubType>() {
                @Override
                public <G> G createGroup(RangedDefinitionVisitor<G> f, RangedDefinitionVisitor.Definition d, G s) {
                    return f.visit(d, type, s);
                }

                @Override
                public Ranged<T, SubType> createGroup(Partial<SubType> subDefinition, PartialQuery<?, Ranged<T, SubType>> query) {
                    return new DefaultRangedGroup<>(subDefinition, type, query);
                }
            };
            return (RangedDefinitionBuilder<T, SubType>) this;
        }

        @Override
        public <T> RangedDefinitionBuilder<T, SubType> withRangeParameter(String name, ParameterType<T> type, Comparator<? super T> comparator) {
            rangeParameter = name;
            factory = (Factory<ValueType, SubType>) new Factory<T, SubType>() {
                @Override
                public <G> G createGroup(RangedDefinitionVisitor<G> factory, RangedDefinitionVisitor.Definition definition, G subGroup) {
                    return factory.visit(definition, type, comparator, subGroup);
                }

                @Override
                public Ranged<T, SubType> createGroup(Partial<SubType> subDefinition, PartialQuery<?, Ranged<T, SubType>> query) {
                    return new DefaultRangedGroup<>(subDefinition, type, query);
                }
            };
            return (RangedDefinitionBuilder<T, SubType>) this;
        }

        @Override
        public <T, B> RangedDefinitionBuilder<T, SubType> withRangeParameter(String name, BasicType<T, B> type) {
            rangeParameter = name;
            factory = (Factory<ValueType, SubType>) new Factory<T, SubType>() {
                @Override
                public <G> G createGroup(RangedDefinitionVisitor<G> factory, RangedDefinitionVisitor.Definition definition, G subGroup) {
                    return factory.visit(definition, type, subGroup);
                }

                @Override
                public Ranged<T, SubType> createGroup(Partial<SubType> subDefinition, PartialQuery<?, Ranged<T, SubType>> query) {
                    return new DefaultRangedGroup<>(subDefinition, type, query);
                }
            };
            return (RangedDefinitionBuilder<T, SubType>) this;
        }

        @Override
        public <T, B> RangedDefinitionBuilder<T, SubType> withRangeParameter(String name, ParameterType<T> type, Function<T, B> converter, BasicType<B, B> basicType) {
            rangeParameter = name;
            factory = (Factory<ValueType, SubType>) new Factory<T, SubType>() {
                @Override
                public <G> G createGroup(RangedDefinitionVisitor<G> factory, RangedDefinitionVisitor.Definition definition, G subGroup) {
                    return factory.visit(definition, type, converter, basicType, subGroup);
                }

                @Override
                public Ranged<T, SubType> createGroup(Partial<SubType> subDefinition, PartialQuery<?, Ranged<T, SubType>> query) {
                    return new DefaultRangedGroup<>(subDefinition, type, query);
                }
            };
            return (RangedDefinitionBuilder<T, SubType>) this;
        }

        @Override
        public <NewSubType extends EntryType> RangedDefinitionBuilder<ValueType, NewSubType> mappingTo(DefinitionBuilder<NewSubType> subGroup) {
            this.subGroup = (DefinitionBuilder<SubType>) subGroup;
            return (RangedDefinitionBuilder<ValueType, NewSubType>) this;
        }

        @Override
        public ParameterGroupDefinition<Ranged<ValueType, SubType>> build(String name, Function<DefinitionBuilderFinalizer, DefinitionBuilderFinalizer> theirFinalizer) {
            subGroupDefinition = subGroup.build(name, myFinalizer().andThen(theirFinalizer));
            return DefaultRangedDefinition.this;
        }

        private Function<DefinitionBuilderFinalizer, DefinitionBuilderFinalizer> myFinalizer() {
            return builder -> builder.prependParameter(rangeParameter);
        }

    }
}
