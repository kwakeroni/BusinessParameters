package be.kwakeroni.parameters.basic.definition.builder;

import be.kwakeroni.parameters.basic.client.model.Mapped;
import be.kwakeroni.parameters.client.api.model.EntryType;
import be.kwakeroni.parameters.definition.ext.DefinitionBuilder;
import be.kwakeroni.parameters.types.api.ParameterType;
import be.kwakeroni.parameters.types.support.ParameterTypes;

/**
 * (C) 2017 Maarten Van Puymbroeck
 */
public interface MappedDefinitionBuilder<KeyType, SubType extends EntryType> extends DefinitionBuilder<Mapped<KeyType, SubType>> {

    public default MappedDefinitionBuilder<String, SubType> withKeyParameter(String name) {
        return withKeyParameter(name, ParameterTypes.STRING);
    }

    public <NewKeyType> MappedDefinitionBuilder<NewKeyType, SubType> withKeyParameter(String name, ParameterType<NewKeyType> type);

    public <NewSubType extends EntryType> MappedDefinitionBuilder<KeyType, NewSubType> mappingTo(DefinitionBuilder<NewSubType> subGroup);

}
