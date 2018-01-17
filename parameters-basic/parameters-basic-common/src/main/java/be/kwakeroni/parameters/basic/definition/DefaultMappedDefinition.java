package be.kwakeroni.parameters.basic.definition;

import be.kwakeroni.parameters.basic.client.model.Mapped;
import be.kwakeroni.parameters.basic.definition.factory.MappedDefinitionVisitor;
import be.kwakeroni.parameters.client.api.BusinessParameters;
import be.kwakeroni.parameters.client.api.model.EntryType;
import be.kwakeroni.parameters.definition.api.DefinitionVisitorContext;
import be.kwakeroni.parameters.definition.api.ParameterGroupDefinition;
import be.kwakeroni.parameters.definition.ext.PartialDefinition;
import be.kwakeroni.parameters.definition.ext.PartialGroup;
import be.kwakeroni.parameters.types.api.ParameterType;

import java.util.Collection;

/**
 * Created by kwakeroni on 11.04.17.
 */
final class DefaultMappedDefinition<GroupType extends EntryType, KeyType, SubType extends EntryType> implements MappedDefinitionVisitor.Definition, PartialDefinition<GroupType, Mapped<KeyType, SubType>> {

    private final String keyParameter;
    private final ParameterType<KeyType> keyType;
    private final PartialDefinition<GroupType, SubType> subGroupDefinition;

    DefaultMappedDefinition(String keyParameter, ParameterType<KeyType> keyType, PartialDefinition<GroupType, SubType> subGroupDefinition) {
        this.keyParameter = keyParameter;
        this.keyType = keyType;
        this.subGroupDefinition = subGroupDefinition;
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
    public Mapped<KeyType, SubType> createGroup(BusinessParameters businessParameters) {
        return createPartial(businessParameters).resolve();
    }

    @Override
    public PartialGroup<GroupType, Mapped<KeyType, SubType>> createPartial(BusinessParameters businessParameters) {
        PartialGroup<GroupType, SubType> subGroup = subGroupDefinition.createPartial(businessParameters);
        return new DefaultMappedGroup<>(keyType, subGroup);
    }

    @Override
    public <G> G apply(DefinitionVisitorContext<G> context) {
        G subGroup = subGroupDefinition.apply(context);
        return MappedDefinitionVisitor.from(context).visit(this, subGroup);
    }

}
