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
            public InMemoryGroup build() {
                return new InmemorySimpleGroup(name, new LinkedHashSet<>(getParameters()));
            }
        }

        return new Simple();
    }

    @Override
    public MappedGroupBuilder<InMemoryGroup> mapped() {
        class Mapped extends MappedGroupBuilderSupport<InMemoryGroup> {
            @Override
            public InMemoryGroup build() {
                InMemoryGroup subGroup = getSubGroup().build();
                return new InmemoryMappedGroup(getKeyParameter(), subGroup);
            }
        }
        return new Mapped();
    }

    @Override
    public RangedGroupBuilder<InMemoryGroup> ranged() {
        class Ranged extends RangedGroupBuilderSupport<InMemoryGroup> {

            @Override
            protected <T extends Comparable<? super T>> InMemoryGroup createGroup(ParameterType<T> type) {
                return new InmemoryRangedGroup(
                        getRangeParameter(),
                        Ranges.stringRangeTypeOf(type),
                        getSubGroup().build()
                );
            }

            @Override
            protected <T> InMemoryGroup createGroup(ParameterType<T> type, Comparator<? super T> comparator) {
                return new InmemoryRangedGroup(
                        getRangeParameter(),
                        Ranges.stringRangeTypeOf(type, comparator),
                        getSubGroup().build()
                );
            }

            @Override
            protected <T, B> InMemoryGroup createGroup(BasicType<T, B> type) {
                Comparator<T> comparator = Comparator.comparing(type::toBasic, type);
                return createGroup(type, comparator);
            }

            @Override
            protected <T, B> InMemoryGroup createGroup(ParameterType<T> type, Function<T, B> converter, BasicType<B, B> basicType) {
                Comparator<T> comparator = Comparator.comparing(converter, basicType);
                return createGroup(type, comparator);
            }
        }
        return new Ranged();
    }
}
