package be.kwakeroni.parameters.basic.connector;

import be.kwakeroni.parameters.client.connector.EntrySet;

/**
 * (C) 2016 Maarten Van Puymbroeck
 */
public interface RangedEntrySet<Q> extends EntrySet<Q> {

    Q getEntryQuery(String value, Q subQuery);

    EntrySet<Q> getSubEntrySet();

}
