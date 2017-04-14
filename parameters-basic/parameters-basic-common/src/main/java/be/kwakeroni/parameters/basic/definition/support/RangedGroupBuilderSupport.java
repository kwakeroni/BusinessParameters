package be.kwakeroni.parameters.basic.definition.support;

import be.kwakeroni.parameters.basic.definition.RangedGroupBuilder;
import be.kwakeroni.parameters.definition.api.GroupBuilder;
import be.kwakeroni.parameters.definition.api.GroupBuilderFinalizer;
import be.kwakeroni.parameters.types.api.ParameterType;
import be.kwakeroni.parameters.types.support.BasicType;

import java.util.Comparator;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Created by kwakeroni on 11.04.17.
 */
public abstract class RangedGroupBuilderSupport<G> implements RangedGroupBuilder<G> {

    private String rangeParameter;
    private GroupBuilder<G> subGroup;
    private Supplier<G> factory;
    private Function<GroupBuilderFinalizer<G>, GroupBuilderFinalizer<G>> finalizer = null;

    @Override
    public <T extends Comparable<? super T>> RangedGroupBuilder<G> withComparableRangeParameter(String name, ParameterType<T> type) {
        this.rangeParameter = name;
        this.factory = () -> createGroup(type);
        return this;
    }

    @Override
    public <T> RangedGroupBuilder<G> withRangeParameter(String name, ParameterType<T> type, Comparator<? super T> comparator) {
        this.rangeParameter = name;
        this.factory = () -> createGroup(type, comparator);
        return this;
    }

    @Override
    public <T, B> RangedGroupBuilder<G> withRangeParameter(String name, BasicType<T, B> type) {
        this.rangeParameter = name;
        this.factory = () -> createGroup(type);
        return this;
    }

    @Override
    public <T, B> RangedGroupBuilder<G> withRangeParameter(String name, ParameterType<T> type, Function<T, B> converter, BasicType<B, B> basicType) {
        this.rangeParameter = name;
        this.factory = () -> createGroup(type, converter, basicType);
        return this;
    }

    @Override
    public RangedGroupBuilder<G> mappingTo(GroupBuilder<G> subGroup) {
        this.subGroup = subGroup;
        return this;
    }


    @Override
    public GroupBuilder<G> finalize(Function<GroupBuilderFinalizer<G>, GroupBuilderFinalizer<G>> theirFinalizer) {
        this.finalizer = (this.finalizer == null) ? theirFinalizer : this.finalizer.andThen(theirFinalizer);
        return this;
    }

    protected String getRangeParameter() {
        return rangeParameter;
    }

    private GroupBuilder<G> getSubGroup() {
        return subGroup;
    }

    protected final G buildSubGroup() {
        return subGroup.finalize(finalizer()).build();
    }

    private Function<GroupBuilderFinalizer<G>, GroupBuilderFinalizer<G>> finalizer() {
        return (finalizer == null) ? myFinalizer() : myFinalizer().andThen(finalizer);
    }

    private Function<GroupBuilderFinalizer<G>, GroupBuilderFinalizer<G>> myFinalizer() {
        return builder -> builder.prependParameter(rangeParameter);
    }

    @Override
    public final G build() {
        return this.factory.get();
    }

    protected abstract <T extends Comparable<? super T>> G createGroup(ParameterType<T> type);

    protected abstract <T> G createGroup(ParameterType<T> type, Comparator<? super T> comparator);

    protected abstract <T, B> G createGroup(BasicType<T, B> type);

    protected abstract <T, B> G createGroup(ParameterType<T> type, Function<T, B> converter, BasicType<B, B> basicType);

}
