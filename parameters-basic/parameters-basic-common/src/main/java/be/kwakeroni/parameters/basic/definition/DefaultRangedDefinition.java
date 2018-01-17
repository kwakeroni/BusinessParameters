package be.kwakeroni.parameters.basic.definition;

import be.kwakeroni.parameters.basic.client.model.Ranged;
import be.kwakeroni.parameters.basic.definition.factory.RangedDefinitionVisitor;
import be.kwakeroni.parameters.client.api.BusinessParameters;
import be.kwakeroni.parameters.client.api.model.EntryType;
import be.kwakeroni.parameters.definition.api.DefinitionVisitorContext;
import be.kwakeroni.parameters.definition.api.ParameterGroupDefinition;
import be.kwakeroni.parameters.definition.ext.PartialDefinition;
import be.kwakeroni.parameters.definition.ext.PartialGroup;

import java.util.Collection;

/**
 * Created by kwakeroni on 11.04.17.
 */
final class DefaultRangedDefinition<GroupType extends EntryType, ValueType, SubType extends EntryType> implements RangedDefinitionVisitor.Definition, PartialDefinition<GroupType, Ranged<ValueType, SubType>> {

    private final String rangeParameter;
    private final Factory<ValueType> factory;
    private final PartialDefinition<GroupType, SubType> subGroupDefinition;

    DefaultRangedDefinition(String rangeParameter, Factory<ValueType> factory, PartialDefinition<GroupType, SubType> subGroupDefinition) {
        this.rangeParameter = rangeParameter;
        this.factory = factory;
        this.subGroupDefinition = subGroupDefinition;
    }

    interface Factory<ValueType> {
        <G> G createGroup(RangedDefinitionVisitor<G> factory, RangedDefinitionVisitor.Definition definition, G subGroup);

        <GroupType extends EntryType, SubType extends EntryType> PartialGroup<GroupType, Ranged<ValueType, SubType>> createGroup(PartialGroup<GroupType, SubType> subDefinition);
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
        return "basic.ranged";
    }

    public String getRangeParameter() {
        return rangeParameter;
    }

    @Override
    public ParameterGroupDefinition<Ranged<ValueType, SubType>> getDefinition() {
        return this;
    }

    @Override
    public Ranged<ValueType, SubType> createGroup(BusinessParameters businessParameters) {
        return createPartial(businessParameters).resolve();
    }

    @Override
    public PartialGroup<GroupType, Ranged<ValueType, SubType>> createPartial(BusinessParameters businessParameters) {
        PartialGroup<GroupType, SubType> subGroup = subGroupDefinition.createPartial(businessParameters);
        return factory.createGroup(subGroup);
    }

    @Override
    public <G> G apply(DefinitionVisitorContext<G> context) {
        G subGroup = subGroupDefinition.apply(context);
        return factory.createGroup(RangedDefinitionVisitor.from(context), this, subGroup);
    }

}
