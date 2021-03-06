package be.kwakeroni.parameters.basic.definition.inmemory;

import be.kwakeroni.parameters.backend.inmemory.api.InMemoryGroup;
import be.kwakeroni.parameters.backend.inmemory.api.InMemoryGroupFactory;
import be.kwakeroni.parameters.basic.backend.inmemory.InmemoryRangedGroup;
import be.kwakeroni.parameters.basic.definition.factory.RangedDefinitionVisitor;
import be.kwakeroni.parameters.basic.type.Ranges;
import be.kwakeroni.parameters.types.api.ParameterType;
import be.kwakeroni.parameters.types.support.BasicType;

import java.util.Comparator;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Created by kwakeroni on 14.04.17.
 */
public class InMemoryRangedGroupFactory implements RangedDefinitionVisitor<InMemoryGroup>, InMemoryGroupFactory {
    @Override
    public void register(Registry registry) {
        registry.register(RangedDefinitionVisitor.class, this);
    }

    @Override
    public void unregister(Consumer<Class<?>> registry) {
        registry.accept(RangedDefinitionVisitor.class);
    }

    @Override
    public <T extends Comparable<? super T>> InMemoryGroup visit(Definition definition, ParameterType<T> type, InMemoryGroup subGroup) {
        return new InmemoryRangedGroup(
                definition.getRangeParameter(),
                Ranges.stringRangeTypeOf(type),
                definition.getDefinition(),
                subGroup
        );
    }

    @Override
    public <T> InMemoryGroup visit(Definition definition, ParameterType<T> type, Comparator<? super T> comparator, InMemoryGroup subGroup) {
        return new InmemoryRangedGroup(
                definition.getRangeParameter(),
                Ranges.stringRangeTypeOf(type, comparator),
                definition.getDefinition(),
                subGroup
        );
    }

    @Override
    public <T, B> InMemoryGroup visit(Definition definition, BasicType<T, B> type, InMemoryGroup subGroup) {
        Comparator<T> comparator = Comparator.comparing(type::toBasic, type);
        return visit(definition, type, comparator, subGroup);
    }

    @Override
    public <T, B> InMemoryGroup visit(Definition definition, ParameterType<T> type, Function<T, B> converter, BasicType<B, B> basicType, InMemoryGroup subGroup) {
        Comparator<T> comparator = Comparator.comparing(converter, basicType);
        return visit(definition, type, comparator, subGroup);
    }

}
