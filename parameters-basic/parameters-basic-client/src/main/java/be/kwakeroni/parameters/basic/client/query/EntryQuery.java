package be.kwakeroni.parameters.basic.client.query;


import be.kwakeroni.parameters.basic.client.model.Entry;
import be.kwakeroni.parameters.basic.client.model.Simple;
import be.kwakeroni.parameters.client.api.model.Parameter;
import be.kwakeroni.parameters.client.api.query.ClientWireFormatterContext;
import be.kwakeroni.parameters.client.api.query.Query;

import java.util.Collection;
import java.util.Collections;

/**
 * (C) 2016 Maarten Van Puymbroeck
 */
public class EntryQuery implements Query<Simple, Entry> {

    private final Collection<Parameter<?>> parameters;

    public EntryQuery() {
        this.parameters = Collections.emptySet();
    }

    public Collection<Parameter<?>> getParameters() {
        return this.parameters;
    }

    @Override
    public Object externalize(ClientWireFormatterContext context) {
        return context.getWireFormatter(BasicClientWireFormatter.class).externalizeEntryQuery(this, context);
    }

    @Override
    public Object externalizeValue(Entry entry, ClientWireFormatterContext context) {
        return context.getWireFormatter(BasicClientWireFormatter.class).externalizeEntry(entry, this, context);
    }

    @Override
    public Entry internalizeResult(Object result, ClientWireFormatterContext context) {
        return context.getWireFormatter(BasicClientWireFormatter.class).internalizeEntry(result, this, context);
    }

    @Override
    public String toString() {
        return "entry";
    }
}
