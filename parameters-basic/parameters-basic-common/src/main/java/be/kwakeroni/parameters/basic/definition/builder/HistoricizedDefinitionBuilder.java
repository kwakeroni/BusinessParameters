package be.kwakeroni.parameters.basic.definition.builder;

import be.kwakeroni.parameters.basic.client.model.Historicized;
import be.kwakeroni.parameters.client.api.model.EntryType;
import be.kwakeroni.parameters.definition.ext.DefinitionBuilder;

public interface HistoricizedDefinitionBuilder<SubType extends EntryType> extends DefinitionBuilder<Historicized<SubType>> {

    public HistoricizedDefinitionBuilder<SubType> withParameter(String name);

    public <NewSubType extends EntryType> HistoricizedDefinitionBuilder<NewSubType> mappingTo(DefinitionBuilder<NewSubType> subGroup);

}
