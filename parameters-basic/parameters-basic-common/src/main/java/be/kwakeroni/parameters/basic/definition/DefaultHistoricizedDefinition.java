package be.kwakeroni.parameters.basic.definition;

import be.kwakeroni.parameters.basic.client.model.Historicized;
import be.kwakeroni.parameters.basic.definition.factory.HistoricizedDefinitionVisitor;
import be.kwakeroni.parameters.client.api.BusinessParameters;
import be.kwakeroni.parameters.client.api.model.EntryType;
import be.kwakeroni.parameters.definition.api.DefinitionVisitorContext;
import be.kwakeroni.parameters.definition.api.ParameterGroupDefinition;
import be.kwakeroni.parameters.definition.ext.PartialDefinition;
import be.kwakeroni.parameters.definition.ext.PartialGroup;

import java.util.Collection;

final class DefaultHistoricizedDefinition<GroupType extends EntryType, SubType extends EntryType> implements HistoricizedDefinitionVisitor.Definition, PartialDefinition<GroupType, Historicized<SubType>> {

    private final String periodParameter;
    private final PartialDefinition<GroupType, SubType> subGroupDefinition;

    public DefaultHistoricizedDefinition(String periodParameter, PartialDefinition<GroupType, SubType> subGroupDefinition) {
        this.periodParameter = periodParameter;
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
    public ParameterGroupDefinition<Historicized<SubType>> getDefinition() {
        return this;
    }

    @Override
    public String getPeriodParameter() {
        return this.periodParameter;
    }

    @Override
    public String getType() {
        return "basic.historicized";
    }

    @Override
    public Historicized<SubType> createGroup(BusinessParameters businessParameters) {
        return createPartial(businessParameters).resolve();
    }

    @Override
    public PartialGroup<GroupType, Historicized<SubType>> createPartial(BusinessParameters businessParameters) {
        PartialGroup<GroupType, SubType> subGroup = subGroupDefinition.createPartial(businessParameters);
        return new DefaultHistoricizedGroup<>(subGroup);
    }

    @Override
    public <G> G apply(DefinitionVisitorContext<G> context) {
        G subGroup = subGroupDefinition.apply(context);
        return HistoricizedDefinitionVisitor.from(context).visit(this, subGroup);
    }
}
