package be.kwakeroni.parameters.definition.ext;

import be.kwakeroni.parameters.client.api.model.EntryType;
import be.kwakeroni.parameters.client.api.query.PartialQuery;

public interface PartialGroup<GroupType extends EntryType, PartialType extends EntryType> {

    default PartialType resolve() {
        return ((PartialGroup<PartialType, PartialType>) this).resolve(PartialQuery.identity());
    }

    public PartialType resolve(PartialQuery<GroupType, PartialType> query);

}