package be.kwakeroni.parameters.basic.definition.descriptor;

import be.kwakeroni.parameters.basic.definition.factory.RangedDefinitionVisitor;
import be.kwakeroni.parameters.definition.api.descriptor.DefinitionDescriptor;
import be.kwakeroni.parameters.definition.api.descriptor.DefinitionDescriptorBuilder;
import be.kwakeroni.parameters.types.api.ParameterType;
import be.kwakeroni.parameters.types.support.BasicType;

import java.util.Comparator;
import java.util.function.Consumer;
import java.util.function.Function;

public class RangedDefinitionDescriptorBuilder implements RangedDefinitionVisitor<DefinitionDescriptor>, DefinitionDescriptorBuilder {

    private static final String RANGE_PARAMETER = "rangeParameter";

    @Override
    public <T extends Comparable<? super T>> DefinitionDescriptor visit(Definition definition, ParameterType<T> type, DefinitionDescriptor subGroup) {
        return visit(definition, subGroup);
    }

    @Override
    public <T> DefinitionDescriptor visit(Definition definition, ParameterType<T> type, Comparator<? super T> comparator, DefinitionDescriptor subGroup) {
        return visit(definition, subGroup);
    }

    @Override
    public <T, B> DefinitionDescriptor visit(Definition definition, BasicType<T, B> type, DefinitionDescriptor subGroup) {
        return visit(definition, subGroup);
    }

    @Override
    public <T, B> DefinitionDescriptor visit(Definition definition, ParameterType<T> type, Function<T, B> converter, BasicType<B, B> basicType, DefinitionDescriptor subGroup) {
        return visit(definition, subGroup);
    }

    private DefinitionDescriptor visit(Definition definition, DefinitionDescriptor subGroup) {
        return DefinitionDescriptor.withType(definition.getDefinition().getType())
                .withSubGroup(subGroup)
                .withProperty(RANGE_PARAMETER, definition.getRangeParameter());
    }

    @Override
    public void register(Registry registry) {
        registry.register(RangedDefinitionVisitor.class, this);
    }

    @Override
    public void unregister(Consumer<Class<?>> registry) {
        registry.accept(RangedDefinitionVisitor.class);
    }
}
