package be.kwakeroni.parameters.basic.definition.builder;

import be.kwakeroni.parameters.basic.client.model.Ranged;
import be.kwakeroni.parameters.client.api.model.EntryType;
import be.kwakeroni.parameters.definition.api.builder.DefinitionBuilder;
import be.kwakeroni.parameters.types.api.ParameterType;
import be.kwakeroni.parameters.types.support.BasicType;

import java.util.Comparator;
import java.util.function.Function;

/**
 * (C) 2017 Maarten Van Puymbroeck
 */
public interface RangedDefinitionBuilder<ValueType, SubType extends EntryType> extends DefinitionBuilder<Ranged<ValueType, SubType>> {

    public <T extends Comparable<? super T>> RangedDefinitionBuilder<T, SubType> withComparableRangeParameter(String name, ParameterType<T> type);

    public <T> RangedDefinitionBuilder<T, SubType> withRangeParameter(String name, ParameterType<T> type, Comparator<? super T> comparator);

    public <T, B> RangedDefinitionBuilder<T, SubType> withRangeParameter(String name, BasicType<T, B> type);

    public <T, B> RangedDefinitionBuilder<T, SubType> withRangeParameter(String name, ParameterType<T> type, Function<T, B> converter, BasicType<B, B> basicType);

    public <NewSubType extends EntryType> RangedDefinitionBuilder<ValueType, NewSubType> mappingTo(DefinitionBuilder<NewSubType> subGroup);

}
