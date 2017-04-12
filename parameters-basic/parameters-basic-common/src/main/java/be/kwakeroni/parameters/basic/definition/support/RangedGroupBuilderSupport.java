package be.kwakeroni.parameters.basic.definition.support;

import be.kwakeroni.parameters.basic.definition.RangedGroupBuilder;
import be.kwakeroni.parameters.definition.api.GroupBuilder;
import be.kwakeroni.parameters.definition.api.GroupBuilderFactoryContext;
import be.kwakeroni.parameters.types.api.ParameterType;
import be.kwakeroni.parameters.types.support.BasicType;

import java.util.Comparator;
import java.util.function.Function;

/**
 * Created by kwakeroni on 11.04.17.
 */
public abstract class RangedGroupBuilderSupport<G> implements RangedGroupBuilder<G> {

    private String rangeParameter;
    private GroupBuilder<G> subGroup;
    private RangedTypeState<G> typeState;

    @Override
    public <T extends Comparable<? super T>> RangedGroupBuilder<G> withComparableRangeParameter(String name, ParameterType<T> type) {
        this.rangeParameter = name;
        this.typeState = context -> createGroup(context, type);
        return this;
    }

    @Override
    public <T> RangedGroupBuilder<G> withRangeParameter(String name, ParameterType<T> type, Comparator<? super T> comparator) {
        this.rangeParameter = name;
        this.typeState = context -> createGroup(context, type, comparator);
        return this;
    }

    @Override
    public <T, B> RangedGroupBuilder<G> withRangeParameter(String name, BasicType<T, B> type) {
        this.rangeParameter = name;
        this.typeState = context -> createGroup(context, type);
        return this;
    }

    @Override
    public <T, B> RangedGroupBuilder<G> withRangeParameter(String name, ParameterType<T> type, Function<T, B> converter, BasicType<B, B> basicType) {
        this.rangeParameter = name;
        this.typeState = context -> createGroup(context, type, converter, basicType);
        return this;
    }

    @Override
    public RangedGroupBuilder<G> mappingTo(GroupBuilder<G> subGroup) {
        this.subGroup = subGroup;
        return this;
    }

    protected String getRangeParameter() {
        return rangeParameter;
    }

    protected GroupBuilder<G> getSubGroup() {
        return subGroup;
    }

    @Override
    public G createGroup(GroupBuilderFactoryContext<G> context) {
        return this.typeState.createGroup(context);
    }

    protected abstract <T extends Comparable<? super T>> G createGroup(GroupBuilderFactoryContext<G> context, ParameterType<T> type);

    protected abstract <T> G createGroup(GroupBuilderFactoryContext<G> context, ParameterType<T> type, Comparator<? super T> comparator);

    protected abstract <T, B> G createGroup(GroupBuilderFactoryContext<G> context, BasicType<T, B> type);

    protected abstract <T, B> G createGroup(GroupBuilderFactoryContext<G> context, ParameterType<T> type, Function<T, B> converter, BasicType<B, B> basicType);

    protected interface RangedTypeState<G> {
        public G createGroup(GroupBuilderFactoryContext<G> context);
    }

}
