package be.kwakeroni.parameters.basic.client.query;

import be.kwakeroni.parameters.basic.client.model.Entry;
import be.kwakeroni.parameters.client.api.query.ClientWireFormatter;
import be.kwakeroni.parameters.client.api.query.ClientWireFormatterContext;

/**
 * (C) 2016 Maarten Van Puymbroeck
 */
public interface BasicClientWireFormatter extends ClientWireFormatter {

    Object externalizeValueQuery(ValueQuery<?> query, ClientWireFormatterContext context);

    Object externalizeEntryQuery(EntryQuery query, ClientWireFormatterContext context);

    Object externalizeMappedQuery(MappedQuery<?, ?, ?> query, ClientWireFormatterContext context);

    Object externalizeRangedQuery(RangedQuery<?, ?, ?> query, ClientWireFormatterContext context);

    <T> Object clientValueToWire(T value, ValueQuery<T> query, ClientWireFormatterContext context);

    <T> T wireToClientValue(Object result, ValueQuery<T> query, ClientWireFormatterContext context);

    Object clientEntryToWire(Entry entry, EntryQuery query, ClientWireFormatterContext context);

    Entry wireToClientEntry(Object result, EntryQuery query, ClientWireFormatterContext context);
}
