package be.kwakeroni.parameters.basic.definition.support;

import be.kwakeroni.parameters.basic.definition.RangedGroupBuilder;
import be.kwakeroni.parameters.definition.api.GroupBuilder;
import be.kwakeroni.parameters.definition.api.GroupBuilderFinalizer;
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
    private Function<G, G> factory;

    @Override
    public <T extends Comparable<? super T>> RangedGroupBuilder<G> withComparableRangeParameter(String name, ParameterType<T> type) {
        this.rangeParameter = name;
        this.factory = subGroup -> createGroup(subGroup, type);
        return this;
    }

    @Override
    public <T> RangedGroupBuilder<G> withRangeParameter(String name, ParameterType<T> type, Comparator<? super T> comparator) {
        this.rangeParameter = name;
        this.factory = subGroup -> createGroup(subGroup, type, comparator);
        return this;
    }

    @Override
    public <T, B> RangedGroupBuilder<G> withRangeParameter(String name, BasicType<T, B> type) {
        this.rangeParameter = name;
        this.factory = subGroup -> createGroup(subGroup, type);
        return this;
    }

    @Override
    public <T, B> RangedGroupBuilder<G> withRangeParameter(String name, ParameterType<T> type, Function<T, B> converter, BasicType<B, B> basicType) {
        this.rangeParameter = name;
        this.factory = subGroup -> createGroup(subGroup, type, converter, basicType);
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

    private GroupBuilder<G> getSubGroup() {
        return subGroup;
    }

    protected G buildSubGroup(Function<GroupBuilderFinalizer<G>, GroupBuilderFinalizer<G>> theirFinalizer) {
        return subGroup.build(myFinalizer().andThen(theirFinalizer));
    }

    private Function<GroupBuilderFinalizer<G>, GroupBuilderFinalizer<G>> myFinalizer() {
        return builder -> builder.prependParameter(rangeParameter);
    }

    @Override
    public final G build(Function<GroupBuilderFinalizer<G>, GroupBuilderFinalizer<G>> finalizer) {
        return this.factory.apply(buildSubGroup(finalizer));
    }

    protected abstract <T extends Comparable<? super T>> G createGroup(G subGroup, ParameterType<T> type);

    protected abstract <T> G createGroup(G subGroup, ParameterType<T> type, Comparator<? super T> comparator);

    protected abstract <T, B> G createGroup(G subGroup, BasicType<T, B> type);

    protected abstract <T, B> G createGroup(G subGroup, ParameterType<T> type, Function<T, B> converter, BasicType<B, B> basicType);

}
