package be.kwakeroni.parameters.basic.definition;

import be.kwakeroni.parameters.definition.api.builder.DefinitionBuilder;
import be.kwakeroni.parameters.types.api.ParameterType;
import be.kwakeroni.parameters.types.support.BasicType;

import java.util.Comparator;
import java.util.function.Function;

/**
 * (C) 2017 Maarten Van Puymbroeck
 */
public interface RangedGroupBuilder extends DefinitionBuilder {

    public <T extends Comparable<? super T>> RangedGroupBuilder withComparableRangeParameter(String name, ParameterType<T> type);

    public <T> RangedGroupBuilder withRangeParameter(String name, ParameterType<T> type, Comparator<? super T> comparator);

    public <T, B> RangedGroupBuilder withRangeParameter(String name, BasicType<T, B> type);

    public <T, B> RangedGroupBuilder withRangeParameter(String name, ParameterType<T> type, Function<T, B> converter, BasicType<B, B> basicType);

    public RangedGroupBuilder mappingTo(DefinitionBuilder subGroup);

}
