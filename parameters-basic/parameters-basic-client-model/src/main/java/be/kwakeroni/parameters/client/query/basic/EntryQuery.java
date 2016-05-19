package be.kwakeroni.parameters.client.query.basic;

import be.kwakeroni.parameters.client.api.entry.Entry;
import be.kwakeroni.parameters.client.api.externalize.ExternalizationContext;
import be.kwakeroni.parameters.client.api.query.Query;
import be.kwakeroni.parameters.client.basic.external.BasicExternalizer;
import be.kwakeroni.parameters.client.model.basic.Simple;

/**
 * (C) 2016 Maarten Van Puymbroeck
 */
public class EntryQuery implements Query<Simple, Entry> {

    @Override
    public Object externalize(ExternalizationContext context) {
        return context.getExternalizer(BasicExternalizer.class).externalizeEntryQuery(this, context);
    }

    @Override
    public String toString() {
        return "entry";
    }
}
