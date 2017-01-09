package be.kwakeroni.parameters.basic.client.query;

import be.kwakeroni.parameters.api.client.query.ExternalizationContext;
import be.kwakeroni.parameters.api.client.query.Externalizer;
import be.kwakeroni.parameters.basic.client.model.Entry;

/**
 * (C) 2016 Maarten Van Puymbroeck
 */
public interface BasicExternalizer extends Externalizer {

    Object externalizeValueQuery(ValueQuery<?> query, ExternalizationContext context);

    Object externalizeEntryQuery(EntryQuery query, ExternalizationContext context);

    Object externalizeMappedQuery(MappedQuery<?, ?, ?> query, ExternalizationContext context);

    Object externalizeRangedQuery(RangedQuery<?, ?, ?> query, ExternalizationContext context);

    <T> T internalizeValue(Object result, ValueQuery<T> query, ExternalizationContext context);

    Entry internalizeEntry(Object result, EntryQuery query, ExternalizationContext context);
}
