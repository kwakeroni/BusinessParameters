package be.kwakeroni.parameters.basic.definition.inmemory;

import be.kwakeroni.parameters.backend.inmemory.api.InMemoryGroup;
import be.kwakeroni.parameters.basic.backend.inmemory.InmemoryMappedGroup;
import be.kwakeroni.parameters.basic.backend.inmemory.InmemoryRangedGroup;
import be.kwakeroni.parameters.basic.backend.inmemory.InmemorySimpleGroup;
import be.kwakeroni.parameters.basic.definition.BasicGroupBuilder;
import be.kwakeroni.parameters.basic.definition.MappedGroupBuilder;
import be.kwakeroni.parameters.basic.definition.RangedGroupBuilder;
import be.kwakeroni.parameters.basic.definition.SimpleGroupBuilder;
import be.kwakeroni.parameters.basic.definition.support.MappedGroupBuilderSupport;
import be.kwakeroni.parameters.basic.definition.support.RangedGroupBuilderSupport;
import be.kwakeroni.parameters.basic.definition.support.SimpleGroupBuilderSupport;
import be.kwakeroni.parameters.basic.type.Ranges;
import be.kwakeroni.parameters.types.api.ParameterType;
import be.kwakeroni.parameters.types.support.BasicType;

import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.function.Function;

/**
 * Created by kwakeroni on 11.04.17.
 */
public class InMemoryBasicGroupBuilder implements BasicGroupBuilder<InMemoryGroup> {

    @Override
    public SimpleGroupBuilder<InMemoryGroup> group(String name) {
        class Simple extends SimpleGroupBuilderSupport<InMemoryGroup> {
            @Override
            public InMemoryGroup createGroup() {
                return new InmemorySimpleGroup(name, new LinkedHashSet<>(getParameters()));
            }
        }

        return new Simple();
    }

    @Override
    public MappedGroupBuilder<InMemoryGroup> mapped() {
        class Mapped extends MappedGroupBuilderSupport<InMemoryGroup> {
            @Override
            protected InMemoryGroup build(InMemoryGroup subGroup) {
                return new InmemoryMappedGroup(getKeyParameter(), subGroup);
            }
        }
        return new Mapped();
    }

    @Override
    public RangedGroupBuilder<InMemoryGroup> ranged() {
        class Ranged extends RangedGroupBuilderSupport<InMemoryGroup> {

            @Override
            protected <T extends Comparable<? super T>> InMemoryGroup createGroup(InMemoryGroup subGroup, ParameterType<T> type) {
                return new InmemoryRangedGroup(
                        getRangeParameter(),
                        Ranges.stringRangeTypeOf(type),
                        subGroup
                );
            }

            @Override
            protected <T> InMemoryGroup createGroup(InMemoryGroup subGroup, ParameterType<T> type, Comparator<? super T> comparator) {
                return new InmemoryRangedGroup(
                        getRangeParameter(),
                        Ranges.stringRangeTypeOf(type, comparator),
                        subGroup
                );
            }

            @Override
            protected <T, B> InMemoryGroup createGroup(InMemoryGroup subGroup, BasicType<T, B> type) {
                Comparator<T> comparator = Comparator.comparing(type::toBasic, type);
                return createGroup(subGroup, type, comparator);
            }

            @Override
            protected <T, B> InMemoryGroup createGroup(InMemoryGroup subGroup, ParameterType<T> type, Function<T, B> converter, BasicType<B, B> basicType) {
                Comparator<T> comparator = Comparator.comparing(converter, basicType);
                return createGroup(subGroup, type, comparator);
            }
        }
        return new Ranged();
    }
}
