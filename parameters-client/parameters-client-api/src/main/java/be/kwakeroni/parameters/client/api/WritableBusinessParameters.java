package be.kwakeroni.parameters.client.api;

import be.kwakeroni.parameters.client.api.model.Entry;
import be.kwakeroni.parameters.client.api.model.EntryType;
import be.kwakeroni.parameters.client.api.model.ParameterGroup;
import be.kwakeroni.parameters.client.api.query.Query;

/**
 * (C) 2017 Maarten Van Puymbroeck
 */
public interface WritableBusinessParameters extends BusinessParameters {

    public <ET extends EntryType, T> void set(ParameterGroup<ET> group, Query<ET, T> query, T value);

    public void addEntry(ParameterGroup<?> group, Entry entry);
}
