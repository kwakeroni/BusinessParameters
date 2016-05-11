package be.kwakeroni.parameters.client.query.basic;

import be.kwakeroni.parameters.client.api.Entry;
import be.kwakeroni.parameters.client.api.Query;
import be.kwakeroni.parameters.client.model.basic.Simple;

/**
 * (C) 2016 Maarten Van Puymbroeck
 */
public class EntryQuery implements Query<Simple, Entry> {

    @Override
    public String toString() {
        return "entry";
    }
}
