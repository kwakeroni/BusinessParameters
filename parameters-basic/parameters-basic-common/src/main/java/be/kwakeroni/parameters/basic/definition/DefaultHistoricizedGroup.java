package be.kwakeroni.parameters.basic.definition;

import be.kwakeroni.parameters.basic.client.model.Historicized;
import be.kwakeroni.parameters.basic.client.query.RangedQuery;
import be.kwakeroni.parameters.client.api.model.EntryType;
import be.kwakeroni.parameters.client.api.query.PartialQuery;
import be.kwakeroni.parameters.definition.ext.PartialGroup;
import be.kwakeroni.parameters.types.support.ParameterTypes;

import java.time.LocalDate;

final class DefaultHistoricizedGroup<GroupType extends EntryType, SubType extends EntryType> implements PartialGroup<GroupType, Historicized<SubType>> {

    private final PartialGroup<GroupType, SubType> subGroupDefinition;

    DefaultHistoricizedGroup(PartialGroup<GroupType, SubType> subGroupDefinition) {
        this.subGroupDefinition = subGroupDefinition;
    }

    @Override
    public Historicized<SubType> resolve(PartialQuery<GroupType, Historicized<SubType>> parentQuery) {
        return new Resolved(parentQuery);
    }

    private final /* value */ class Resolved implements Historicized<SubType> {

        private final PartialQuery<GroupType, Historicized<SubType>> parentQuery;

        Resolved(PartialQuery<GroupType, Historicized<SubType>> parentQuery) {
            this.parentQuery = parentQuery;
        }

        @Override
        public SubType at(LocalDate value) {
            return resolveSubGroup(new RangedQuery.Partial<>(value, ParameterTypes.LOCAL_DATE));
        }

        private SubType resolveSubGroup(PartialQuery<Historicized<SubType>, SubType> myQueryPart) {
            PartialQuery<GroupType, SubType> myQuery = parentQuery.andThen(myQueryPart);
            return subGroupDefinition.resolve(myQuery);
        }
    }

}
