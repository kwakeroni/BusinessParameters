package be.kwakeroni.parameters.basic.definition;

import be.kwakeroni.parameters.basic.client.model.Mapped;
import be.kwakeroni.parameters.basic.client.query.MappedQuery;
import be.kwakeroni.parameters.client.api.model.EntryType;
import be.kwakeroni.parameters.client.api.query.PartialQuery;
import be.kwakeroni.parameters.definition.api.ParameterGroupDefinition;
import be.kwakeroni.parameters.types.api.ParameterType;

/**
 * Created by kwakeroni on 20/11/17.
 */
final class DefaultMappedGroup<KeyType, SubType extends EntryType> implements Mapped<KeyType, SubType> {

    private final ParameterGroupDefinition.Partial<SubType> subDefinition;
    private final ParameterType<KeyType> keyType;
    private final PartialQuery<?, Mapped<KeyType, SubType>> partial;

    public DefaultMappedGroup(ParameterGroupDefinition.Partial<SubType> subDefinition, ParameterType<KeyType> keyType, PartialQuery<?, Mapped<KeyType, SubType>> partial) {
        this.subDefinition = subDefinition;
        this.keyType = keyType;
        this.partial = partial;
    }

    @Override
    public SubType forKey(KeyType key) {
        PartialQuery<Mapped<KeyType, SubType>, SubType> mappedQuery = new MappedQuery.Partial<>(key, keyType);
        return createSubType(partial.andThen(mappedQuery));
    }

    private SubType createSubType(PartialQuery<?, SubType> partialQuery) {
        return subDefinition.createGroup(partialQuery);
    }

}
