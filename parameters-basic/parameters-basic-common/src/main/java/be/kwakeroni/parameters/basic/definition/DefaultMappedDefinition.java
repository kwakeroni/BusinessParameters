package be.kwakeroni.parameters.basic.definition;

import be.kwakeroni.parameters.basic.client.model.Mapped;
import be.kwakeroni.parameters.basic.definition.builder.MappedDefinitionBuilder;
import be.kwakeroni.parameters.basic.definition.factory.MappedDefinitionVisitor;
import be.kwakeroni.parameters.client.api.BusinessParameters;
import be.kwakeroni.parameters.client.api.model.EntryType;
import be.kwakeroni.parameters.definition.api.DefinitionVisitorContext;
import be.kwakeroni.parameters.definition.api.ParameterGroupDefinition;
import be.kwakeroni.parameters.definition.api.builder.DefinitionBuilder;
import be.kwakeroni.parameters.definition.api.builder.DefinitionBuilderFinalizer;
import be.kwakeroni.parameters.types.api.ParameterType;

import java.util.Collection;
import java.util.function.Function;

/**
 * Created by kwakeroni on 11.04.17.
 */
final class DefaultMappedDefinition<KeyType, SubType extends EntryType> implements MappedDefinitionVisitor.Definition, ParameterGroupDefinition<Mapped<KeyType, SubType>> {

    private String keyParameter;
    private ParameterType<KeyType> keyType;
    private ParameterGroupDefinition<SubType> subGroupDefinition;

    private DefaultMappedDefinition() {

    }

    @Override
    public String getName() {
        return subGroupDefinition.getName();
    }

    @Override
    public Collection<String> getParameters() {
        return subGroupDefinition.getParameters();
    }

    @Override
    public String getType() {
        return "basic.mapped";
    }

    @Override
    public String getKeyParameter() {
        return keyParameter;
    }

    @Override
    public ParameterGroupDefinition<Mapped<KeyType, SubType>> getDefinition() {
        return this;
    }

    @Override
    public Partial<Mapped<KeyType, SubType>> createPartial(BusinessParameters businessParameters) {
        Partial<SubType> subDefinition = subGroupDefinition.createPartial(businessParameters);
        return partialQuery -> new DefaultMappedGroup<>(subDefinition, keyType, partialQuery);
    }

    @Override
    public <G> G apply(DefinitionVisitorContext<G> context) {
        G subGroup = subGroupDefinition.apply(context);
        return MappedDefinitionVisitor.from(context).visit(this, subGroup);
    }

    static DefaultMappedDefinition<?, ?>.Builder builder() {
        return new DefaultMappedDefinition<>().new Builder();
    }

    private final class Builder implements MappedDefinitionBuilder<KeyType, SubType> {
        private DefinitionBuilder<SubType> subGroup;

        @Override
        public <NewKeyType> MappedDefinitionBuilder<NewKeyType, SubType> withKeyParameter(String name, ParameterType<NewKeyType> type) {
            keyParameter = name;
            keyType = (ParameterType<KeyType>) type;
            return (MappedDefinitionBuilder<NewKeyType, SubType>) this;
        }

        @Override
        public <NewSubType extends EntryType> MappedDefinitionBuilder<KeyType, NewSubType> mappingTo(DefinitionBuilder<NewSubType> subGroup) {
            this.subGroup = (DefinitionBuilder<SubType>) subGroup;
            return (MappedDefinitionBuilder<KeyType, NewSubType>) this;
        }

        @Override
        public ParameterGroupDefinition<Mapped<KeyType, SubType>> build(String name, Function<DefinitionBuilderFinalizer, DefinitionBuilderFinalizer> theirFinalizer) {
            subGroupDefinition = subGroup.build(name, myFinalizer().andThen(theirFinalizer));
            return DefaultMappedDefinition.this;
        }

        private Function<DefinitionBuilderFinalizer, DefinitionBuilderFinalizer> myFinalizer() {
            return builder -> builder.prependParameter(keyParameter);
        }
    }
}
