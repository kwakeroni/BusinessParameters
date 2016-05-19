package be.kwakeroni.parameters.client.api.entry;

import be.kwakeroni.parameters.client.api.query.PartialQuery;
import be.kwakeroni.parameters.client.model.EntryType;

/**
 * Represents entry types in the form of partial queries.
 *
 * (C) 2016 Maarten Van Puymbroeck
 */
@FunctionalInterface
public interface EntryTypeBuilder<Target extends EntryType, Built extends EntryType> {

    Built by(PartialQuery<Target, Built> retrieveEntry);

    default Built getEntryType() {
        // Target == Built
        EntryTypeBuilder<Built, Built> self = (EntryTypeBuilder<Built, Built>) this;
        return self.by(PartialQuery.startQuery());
    }
}
