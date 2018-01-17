package be.kwakeroni.parameters.basic.definition;

import be.kwakeroni.parameters.basic.client.model.Ranged;
import be.kwakeroni.parameters.basic.client.query.RangedQuery;
import be.kwakeroni.parameters.client.api.model.EntryType;
import be.kwakeroni.parameters.client.api.query.PartialQuery;
import be.kwakeroni.parameters.definition.ext.PartialGroup;
import be.kwakeroni.parameters.types.api.ParameterType;

final class DefaultRangedGroup<GroupType extends EntryType, ValueType, SubType extends EntryType> implements PartialGroup<GroupType, Ranged<ValueType, SubType>> {

    private final PartialGroup<GroupType, SubType> subGroupDefinition;
    private final ParameterType<ValueType> valueType;

    DefaultRangedGroup(ParameterType<ValueType> valueType, PartialGroup<GroupType, SubType> subGroupDefinition) {
        this.subGroupDefinition = subGroupDefinition;
        this.valueType = valueType;
    }

    @Override
    public Ranged<ValueType, SubType> resolve(PartialQuery<GroupType, Ranged<ValueType, SubType>> parentQuery) {
        return new Resolved(parentQuery);
    }

    private final /* value */ class Resolved implements Ranged<ValueType, SubType> {
        private final PartialQuery<GroupType, Ranged<ValueType, SubType>> parentQuery;

        Resolved(PartialQuery<GroupType, Ranged<ValueType, SubType>> parentQuery) {
            this.parentQuery = parentQuery;
        }

        @Override
        public SubType at(ValueType value) {
            return resolveSubGroup(new RangedQuery.Partial<>(value, valueType));
        }

        private SubType resolveSubGroup(PartialQuery<Ranged<ValueType, SubType>, SubType> myQueryPart) {
            PartialQuery<GroupType, SubType> myQuery = parentQuery.andThen(myQueryPart);
            return subGroupDefinition.resolve(myQuery);
        }

    }
}
