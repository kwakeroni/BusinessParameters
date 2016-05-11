package be.kwakeroni.parameters.client.api;

import be.kwakeroni.parameters.client.model.EntryType;

/**
 * (C) 2016 Maarten Van Puymbroeck
 */
@FunctionalInterface
public interface EntryTypeBuilder<ParentLocation extends EntryType, Location extends EntryType> {

    ParentLocation toEntryType();

}
