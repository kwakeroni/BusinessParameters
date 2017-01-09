package be.kwakeroni.parameters.basic.client.query;


import be.kwakeroni.parameters.api.client.query.ClientWireFormatterContext;
import be.kwakeroni.parameters.api.client.query.Query;
import be.kwakeroni.parameters.basic.client.model.Entry;
import be.kwakeroni.parameters.basic.client.model.Simple;

/**
 * (C) 2016 Maarten Van Puymbroeck
 */
public class EntryQuery implements Query<Simple, Entry> {

    @Override
    public Object externalize(ClientWireFormatterContext context) {
        return context.getWireFormatter(BasicClientWireFormatter.class).externalizeEntryQuery(this, context);
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
