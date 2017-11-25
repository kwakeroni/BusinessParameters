package be.kwakeroni.parameters.basic.definition;

import be.kwakeroni.parameters.basic.client.model.Ranged;
import be.kwakeroni.parameters.basic.definition.DefaultRangedDefinition.Factory;
import be.kwakeroni.parameters.basic.definition.builder.RangedDefinitionBuilder;
import be.kwakeroni.parameters.basic.definition.factory.RangedDefinitionVisitor;
import be.kwakeroni.parameters.client.api.model.EntryType;
import be.kwakeroni.parameters.definition.ext.DefinitionBuilder;
import be.kwakeroni.parameters.definition.ext.DefinitionBuilderFinalizer;
import be.kwakeroni.parameters.definition.ext.PartialDefinition;
import be.kwakeroni.parameters.definition.ext.PartialGroup;
import be.kwakeroni.parameters.types.api.ParameterType;
import be.kwakeroni.parameters.types.support.BasicType;

import java.util.Comparator;
import java.util.function.Function;

final /* value */ class DefaultRangedDefinitionBuilder<ValueType, SubType extends EntryType> implements RangedDefinitionBuilder<ValueType, SubType> {
    private String rangeParameter;
    private Factory<ValueType> factory;
    private DefinitionBuilder<SubType> subGroup;

    private <NewValueType, NewSubType extends EntryType>
    DefaultRangedDefinitionBuilder<NewValueType, NewSubType> with(Factory<NewValueType> factory, DefinitionBuilder<NewSubType> subGroup) {
        DefaultRangedDefinitionBuilder<NewValueType, NewSubType> self = (DefaultRangedDefinitionBuilder<NewValueType, NewSubType>) this;
        self.factory = factory;
        self.subGroup = subGroup;
        return self;
    }

    @Override
    public PartialDefinition<?, Ranged<ValueType, SubType>> createPartialDefinition(String name, Function<DefinitionBuilderFinalizer, DefinitionBuilderFinalizer> theirFinalizer) {
        PartialDefinition<?, SubType> subGroupDefinition = subGroup.createPartialDefinition(name, myFinalizer().andThen(theirFinalizer));
        return new DefaultRangedDefinition<>(rangeParameter, factory, subGroupDefinition);
    }

    @Override
    public <NewSubType extends EntryType> RangedDefinitionBuilder<ValueType, NewSubType> mappingTo(DefinitionBuilder<NewSubType> subGroup) {
        return with(factory, subGroup);
    }


    private <NewValueType> RangedDefinitionBuilder<NewValueType, SubType> withRangeParameter(String name, Factory<NewValueType> factory) {
        this.rangeParameter = name;
        return with(factory, subGroup);
    }

    @Override
    public <T extends Comparable<? super T>> RangedDefinitionBuilder<T, SubType> withComparableRangeParameter(String name, ParameterType<T> type) {
        return withRangeParameter(name, comparableTypeFactory(type));
    }

    private static <T extends Comparable<? super T>> Factory<T> comparableTypeFactory(ParameterType<T> type) {
        return new Factory<T>() {
            @Override
            public <G> G createGroup(RangedDefinitionVisitor<G> f, RangedDefinitionVisitor.Definition d, G s) {
                return f.visit(d, type, s);
            }

            @Override
            public <GroupType extends EntryType, SubType extends EntryType> PartialGroup<GroupType, Ranged<T, SubType>> createGroup(PartialGroup<GroupType, SubType> subDefinition) {
                return new DefaultRangedGroup<>(type, subDefinition);
            }
        };
    }

    @Override
    public <T> RangedDefinitionBuilder<T, SubType> withRangeParameter(String name, ParameterType<T> type, Comparator<? super T> comparator) {
        return withRangeParameter(name, comparingFactory(type, comparator));
    }

    private static <T> Factory<T> comparingFactory(ParameterType<T> type, Comparator<? super T> comparator) {
        return new Factory<T>() {
            @Override
            public <G> G createGroup(RangedDefinitionVisitor<G> factory, RangedDefinitionVisitor.Definition definition, G subGroup) {
                return factory.visit(definition, type, comparator, subGroup);
            }

            @Override
            public <GroupType extends EntryType, SubType extends EntryType> PartialGroup<GroupType, Ranged<T, SubType>> createGroup(PartialGroup<GroupType, SubType> subDefinition) {
                return new DefaultRangedGroup<>(type, subDefinition);
            }
        };
    }

    @Override
    public <T, B> RangedDefinitionBuilder<T, SubType> withRangeParameter(String name, BasicType<T, B> type) {
        return withRangeParameter(name, basicTypeFactory(type));
    }

    private static <T, B> Factory<T> basicTypeFactory(BasicType<T, B> type) {
        return new Factory<T>() {
            @Override
            public <G> G createGroup(RangedDefinitionVisitor<G> factory, RangedDefinitionVisitor.Definition definition, G subGroup) {
                return factory.visit(definition, type, subGroup);
            }

            @Override
            public <GroupType extends EntryType, SubType extends EntryType> PartialGroup<GroupType, Ranged<T, SubType>> createGroup(PartialGroup<GroupType, SubType> subDefinition) {
                return new DefaultRangedGroup<>(type, subDefinition);
            }
        };
    }

    @Override
    public <T, B> RangedDefinitionBuilder<T, SubType> withRangeParameter(String name, ParameterType<T> type, Function<T, B> converter, BasicType<B, B> basicType) {
        return withRangeParameter(name, convertingTypeFactory(type, converter, basicType));
    }

    private static <T, B> Factory<T> convertingTypeFactory(ParameterType<T> type, Function<T, B> converter, BasicType<B, B> basicType) {
        return new Factory<T>() {
            @Override
            public <G> G createGroup(RangedDefinitionVisitor<G> factory, RangedDefinitionVisitor.Definition definition, G subGroup) {
                return factory.visit(definition, type, converter, basicType, subGroup);
            }

            @Override
            public <GroupType extends EntryType, SubType extends EntryType> PartialGroup<GroupType, Ranged<T, SubType>> createGroup(PartialGroup<GroupType, SubType> subDefinition) {
                return new DefaultRangedGroup<>(type, subDefinition);
            }
        };
    }

    private Function<DefinitionBuilderFinalizer, DefinitionBuilderFinalizer> myFinalizer() {
        return builder -> builder.prependParameter(rangeParameter);
    }
}
