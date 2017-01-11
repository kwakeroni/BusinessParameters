package be.kwakeroni.parameters.basic.client.query;

import be.kwakeroni.parameters.client.api.query.ClientWireFormatterContext;
import be.kwakeroni.parameters.client.api.query.ClientWireFormatter;
import be.kwakeroni.parameters.basic.client.model.Entry;

/**
 * (C) 2016 Maarten Van Puymbroeck
 */
public interface BasicClientWireFormatter extends ClientWireFormatter {

    Object externalizeValueQuery(ValueQuery<?> query, ClientWireFormatterContext context);

    Object externalizeEntryQuery(EntryQuery query, ClientWireFormatterContext context);

    Object externalizeMappedQuery(MappedQuery<?, ?, ?> query, ClientWireFormatterContext context);

    Object externalizeRangedQuery(RangedQuery<?, ?, ?> query, ClientWireFormatterContext context);

    <T> T internalizeValue(Object result, ValueQuery<T> query, ClientWireFormatterContext context);

    Entry internalizeEntry(Object result, EntryQuery query, ClientWireFormatterContext context);
}
