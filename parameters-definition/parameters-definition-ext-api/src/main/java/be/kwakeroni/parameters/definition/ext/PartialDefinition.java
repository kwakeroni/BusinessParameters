package be.kwakeroni.parameters.definition.ext;

import be.kwakeroni.parameters.client.api.BusinessParameters;
import be.kwakeroni.parameters.client.api.model.EntryType;
import be.kwakeroni.parameters.definition.api.ParameterGroupDefinition;

public interface PartialDefinition<GroupType extends EntryType, PartialType extends EntryType> extends ParameterGroupDefinition<PartialType> {

    public PartialGroup<GroupType, PartialType> createPartial(BusinessParameters businessParameters);

}
