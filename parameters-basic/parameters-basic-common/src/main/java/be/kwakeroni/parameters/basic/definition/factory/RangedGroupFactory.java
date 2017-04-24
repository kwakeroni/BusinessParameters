package be.kwakeroni.parameters.basic.definition.factory;

import be.kwakeroni.parameters.definition.api.factory.GroupFactory;
import be.kwakeroni.parameters.definition.api.factory.GroupFactoryContext;
import be.kwakeroni.parameters.types.api.ParameterType;
import be.kwakeroni.parameters.types.support.BasicType;

import java.util.Comparator;
import java.util.function.Function;

/**
 * Created by kwakeroni on 14.04.17.
 */
public interface RangedGroupFactory<G> extends GroupFactory<G> {

    public <T extends Comparable<? super T>> G createGroup(RangedGroupFactory.Definition definition, ParameterType<T> type, G subGroup);

    public <T> G createGroup(RangedGroupFactory.Definition definition, ParameterType<T> type, Comparator<? super T> comparator, G subGroup);

    public <T, B> G createGroup(RangedGroupFactory.Definition definition, BasicType<T, B> type, G subGroup);

    public <T, B> G createGroup(RangedGroupFactory.Definition definition, ParameterType<T> type, Function<T, B> converter, BasicType<B, B> basicType, G subGroup);

    public static <G> RangedGroupFactory<G> from(GroupFactoryContext<G> context) {
        return (RangedGroupFactory<G>) context.getFactory(RangedGroupFactory.class);
    }

    public interface Definition {
        public String getRangeParameter();
    }
}
