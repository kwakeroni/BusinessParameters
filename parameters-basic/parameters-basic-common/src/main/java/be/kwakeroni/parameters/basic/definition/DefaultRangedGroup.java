package be.kwakeroni.parameters.basic.definition;

import be.kwakeroni.parameters.basic.client.model.Ranged;
import be.kwakeroni.parameters.basic.client.query.RangedQuery;
import be.kwakeroni.parameters.client.api.model.EntryType;
import be.kwakeroni.parameters.client.api.query.PartialQuery;
import be.kwakeroni.parameters.definition.api.ParameterGroupDefinition;
import be.kwakeroni.parameters.types.api.ParameterType;

public class DefaultRangedGroup<ValueType, SubType extends EntryType> implements Ranged<ValueType, SubType> {

    private final ParameterGroupDefinition.Partial<SubType> subDefinition;
    private final ParameterType<ValueType> valueType;
    private final PartialQuery<?, Ranged<ValueType, SubType>> partialQuery;

    public DefaultRangedGroup(ParameterGroupDefinition.Partial<SubType> subDefinition, ParameterType<ValueType> valueType, PartialQuery<?, Ranged<ValueType, SubType>> partialQuery) {
        this.subDefinition = subDefinition;
        this.valueType = valueType;
        this.partialQuery = partialQuery;
    }

    @Override
    public SubType at(ValueType value) {
        PartialQuery<Ranged<ValueType, SubType>, SubType> rangedQuery = new RangedQuery.Partial<>(value, valueType);
        PartialQuery<?, SubType> query = partialQuery.andThen(rangedQuery);
        return subDefinition.createGroup(query);
    }
}
