package be.kwakeroni.parameters.basic.connector;

import be.kwakeroni.parameters.client.connector.EntrySet;

/**
 * (C) 2016 Maarten Van Puymbroeck
 */
public interface SimpleEntrySet<Q> extends EntrySet<Q> {

    Q getEntryQuery();

    Q getValueQuery(String parameterName);
}
