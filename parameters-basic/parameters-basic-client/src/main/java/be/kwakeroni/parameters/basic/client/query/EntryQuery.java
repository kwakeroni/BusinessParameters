package be.kwakeroni.parameters.basic.client.query;

import be.kwakeroni.parameters.api.client.model.Entry;
import be.kwakeroni.parameters.api.client.query.ExternalizationContext;
import be.kwakeroni.parameters.api.client.query.Query;
import be.kwakeroni.parameters.basic.client.model.Simple;

/**
 * (C) 2016 Maarten Van Puymbroeck
 */
public class EntryQuery implements Query<Simple, Entry> {

    @Override
    public Object externalize(ExternalizationContext context) {
        return context.getExternalizer(BasicExternalizer.class).externalizeEntryQuery(this, context);
    }

    @Override
    public Entry internalize(Object result, ExternalizationContext context) {
        return context.getExternalizer(BasicExternalizer.class).internalizeEntry(result, this, context);
    }

    @Override
    public String toString() {
        return "entry";
    }
}
