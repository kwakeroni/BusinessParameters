package be.kwakeroni.parameters.basic.definition.inmemory;

import be.kwakeroni.parameters.backend.inmemory.api.InMemoryGroup;
import be.kwakeroni.parameters.basic.backend.inmemory.InmemoryRangedGroup;
import be.kwakeroni.parameters.basic.definition.factory.RangedGroupFactory;
import be.kwakeroni.parameters.basic.type.Ranges;
import be.kwakeroni.parameters.types.api.ParameterType;
import be.kwakeroni.parameters.types.support.BasicType;

import java.util.Comparator;
import java.util.function.Function;

/**
 * Created by kwakeroni on 14.04.17.
 */
public class InMemoryRangedGroupFactory implements RangedGroupFactory<InMemoryGroup> {

    @Override
    public <T extends Comparable<? super T>> InMemoryGroup createGroup(Definition definition, ParameterType<T> type, InMemoryGroup subGroup) {
        return new InmemoryRangedGroup(
                definition.getRangeParameter(),
                Ranges.stringRangeTypeOf(type),
                definition.getDefinition(),
                subGroup
        );
    }

    @Override
    public <T> InMemoryGroup createGroup(Definition definition, ParameterType<T> type, Comparator<? super T> comparator, InMemoryGroup subGroup) {
        return new InmemoryRangedGroup(
                definition.getRangeParameter(),
                Ranges.stringRangeTypeOf(type, comparator),
                definition.getDefinition(),
                subGroup
        );
    }

    @Override
    public <T, B> InMemoryGroup createGroup(Definition definition, BasicType<T, B> type, InMemoryGroup subGroup) {
        Comparator<T> comparator = Comparator.comparing(type::toBasic, type);
        return createGroup(definition, type, comparator, subGroup);
    }

    @Override
    public <T, B> InMemoryGroup createGroup(Definition definition, ParameterType<T> type, Function<T, B> converter, BasicType<B, B> basicType, InMemoryGroup subGroup) {
        Comparator<T> comparator = Comparator.comparing(converter, basicType);
        return createGroup(definition, type, comparator, subGroup);
    }

}
