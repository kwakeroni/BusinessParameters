package be.kwakeroni.parameters.basic.definition;

import be.kwakeroni.parameters.basic.client.model.Historicized;
import be.kwakeroni.parameters.basic.definition.builder.HistoricizedDefinitionBuilder;
import be.kwakeroni.parameters.client.api.model.EntryType;
import be.kwakeroni.parameters.definition.ext.DefinitionBuilder;
import be.kwakeroni.parameters.definition.ext.DefinitionBuilderFinalizer;
import be.kwakeroni.parameters.definition.ext.PartialDefinition;

import java.util.function.Function;

public class DefaultHistoricizedDefinitionBuilder<SubType extends EntryType> implements HistoricizedDefinitionBuilder<SubType> {

    private String periodParameter;
    private DefinitionBuilder<SubType> subGroup;

    @Override
    public HistoricizedDefinitionBuilder<SubType> withParameter(String name) {
        this.periodParameter = name;
        return this;
    }

    @Override
    public <NewSubType extends EntryType> HistoricizedDefinitionBuilder<NewSubType> mappingTo(DefinitionBuilder<NewSubType> subGroup) {
        DefaultHistoricizedDefinitionBuilder<NewSubType> self = (DefaultHistoricizedDefinitionBuilder<NewSubType>) this;
        self.subGroup = subGroup;
        return self;
    }

    @Override
    public PartialDefinition<?, Historicized<SubType>> createPartialDefinition(String name, Function<DefinitionBuilderFinalizer, DefinitionBuilderFinalizer> theirFinalizer) {
        PartialDefinition<?, SubType> subGroupDefinition = subGroup.createPartialDefinition(name, myFinalizer().andThen(theirFinalizer));
        return new DefaultHistoricizedDefinition<>(this.periodParameter, subGroupDefinition);
    }

    private Function<DefinitionBuilderFinalizer, DefinitionBuilderFinalizer> myFinalizer() {
        return builder -> builder.prependParameter(periodParameter);
    }
}
