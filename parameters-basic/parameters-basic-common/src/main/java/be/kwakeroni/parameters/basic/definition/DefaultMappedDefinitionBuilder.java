package be.kwakeroni.parameters.basic.definition;

import be.kwakeroni.parameters.basic.client.model.Mapped;
import be.kwakeroni.parameters.basic.definition.builder.MappedDefinitionBuilder;
import be.kwakeroni.parameters.client.api.model.EntryType;
import be.kwakeroni.parameters.definition.ext.DefinitionBuilder;
import be.kwakeroni.parameters.definition.ext.DefinitionBuilderFinalizer;
import be.kwakeroni.parameters.definition.ext.PartialDefinition;
import be.kwakeroni.parameters.types.api.ParameterType;

import java.util.function.Function;

final /* value */ class DefaultMappedDefinitionBuilder<KeyType, SubType extends EntryType> implements MappedDefinitionBuilder<KeyType, SubType> {
    private String keyParameter;
    private ParameterType<KeyType> keyType;
    private DefinitionBuilder<SubType> subGroup;

    private <NewKeyType, NewSubType extends EntryType>
    DefaultMappedDefinitionBuilder<NewKeyType, NewSubType> with(ParameterType<NewKeyType> key, DefinitionBuilder<NewSubType> subGroup) {
        DefaultMappedDefinitionBuilder<NewKeyType, NewSubType> self = (DefaultMappedDefinitionBuilder<NewKeyType, NewSubType>) this;
        self.keyType = key;
        self.subGroup = subGroup;
        return self;
    }

    @Override
    public <NewKeyType> MappedDefinitionBuilder<NewKeyType, SubType> withKeyParameter(String keyParameter, ParameterType<NewKeyType> keyType) {
        this.keyParameter = keyParameter;
        return with(keyType, subGroup);
    }

    @Override
    public <NewSubType extends EntryType> MappedDefinitionBuilder<KeyType, NewSubType> mappingTo(DefinitionBuilder<NewSubType> subGroup) {
        return with(keyType, subGroup);
    }

    @Override
    public PartialDefinition<?, Mapped<KeyType, SubType>> createPartialDefinition(String name, Function<DefinitionBuilderFinalizer, DefinitionBuilderFinalizer> theirFinalizer) {
        PartialDefinition<?, SubType> subGroupDefinition = subGroup.createPartialDefinition(name, myFinalizer().andThen(theirFinalizer));
        return new DefaultMappedDefinition<>(keyParameter, keyType, subGroupDefinition);
    }

    private Function<DefinitionBuilderFinalizer, DefinitionBuilderFinalizer> myFinalizer() {
        return builder -> builder.prependParameter(keyParameter);
    }
}
