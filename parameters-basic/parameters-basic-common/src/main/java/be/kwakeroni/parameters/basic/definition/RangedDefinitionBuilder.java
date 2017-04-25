package be.kwakeroni.parameters.basic.definition;

import be.kwakeroni.parameters.definition.api.builder.DefinitionBuilder;
import be.kwakeroni.parameters.types.api.ParameterType;
import be.kwakeroni.parameters.types.support.BasicType;

import java.util.Comparator;
import java.util.function.Function;

/**
 * (C) 2017 Maarten Van Puymbroeck
 */
public interface RangedDefinitionBuilder extends DefinitionBuilder {

    public <T extends Comparable<? super T>> RangedDefinitionBuilder withComparableRangeParameter(String name, ParameterType<T> type);

    public <T> RangedDefinitionBuilder withRangeParameter(String name, ParameterType<T> type, Comparator<? super T> comparator);

    public <T, B> RangedDefinitionBuilder withRangeParameter(String name, BasicType<T, B> type);

    public <T, B> RangedDefinitionBuilder withRangeParameter(String name, ParameterType<T> type, Function<T, B> converter, BasicType<B, B> basicType);

    public RangedDefinitionBuilder mappingTo(DefinitionBuilder subGroup);

}
