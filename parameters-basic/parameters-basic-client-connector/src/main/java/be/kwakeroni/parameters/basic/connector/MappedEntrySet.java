package be.kwakeroni.parameters.basic.connector;

import be.kwakeroni.parameters.client.connector.EntrySet;

/**
 * (C) 2016 Maarten Van Puymbroeck
 */
public interface MappedEntrySet<Q> extends EntrySet<Q> {

    Q getEntryQuery(String keyValue, Q subQuery);

    EntrySet<Q> getSubEntrySet();

}
