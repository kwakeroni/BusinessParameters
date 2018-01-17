package be.kwakeroni.parameters.basic.definition;

import be.kwakeroni.parameters.basic.client.model.Mapped;
import be.kwakeroni.parameters.basic.client.query.MappedQuery;
import be.kwakeroni.parameters.client.api.model.EntryType;
import be.kwakeroni.parameters.client.api.query.PartialQuery;
import be.kwakeroni.parameters.definition.ext.PartialGroup;
import be.kwakeroni.parameters.types.api.ParameterType;

final class DefaultMappedGroup<GroupType extends EntryType, KeyType, SubType extends EntryType> implements PartialGroup<GroupType, Mapped<KeyType, SubType>> {

    private final ParameterType<KeyType> keyType;
    private final PartialGroup<GroupType, SubType> subGroupDefinition;

    DefaultMappedGroup(ParameterType<KeyType> keyType, PartialGroup<GroupType, SubType> subGroupDefinition) {
        this.keyType = keyType;
        this.subGroupDefinition = subGroupDefinition;
    }

    @Override
    public Mapped<KeyType, SubType> resolve(PartialQuery<GroupType, Mapped<KeyType, SubType>> parentQuery) {
        return new Resolved(parentQuery);
    }

    private final /* value */ class Resolved implements Mapped<KeyType, SubType> {
        private final PartialQuery<GroupType, Mapped<KeyType, SubType>> parentQuery;

        Resolved(PartialQuery<GroupType, Mapped<KeyType, SubType>> parentQuery) {
            this.parentQuery = parentQuery;
        }

        @Override
        public SubType forKey(KeyType key) {
            return resolveSubGroup(new MappedQuery.Partial<>(key, keyType));
        }

        private SubType resolveSubGroup(PartialQuery<Mapped<KeyType, SubType>, SubType> myQueryPart) {
            PartialQuery<GroupType, SubType> myQuery = parentQuery.andThen(myQueryPart);
            return subGroupDefinition.resolve(myQuery);
        }
    }
}
