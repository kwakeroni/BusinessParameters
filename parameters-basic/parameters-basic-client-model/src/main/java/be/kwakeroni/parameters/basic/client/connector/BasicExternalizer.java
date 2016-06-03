package be.kwakeroni.parameters.basic.client.connector;

import be.kwakeroni.parameters.basic.client.query.EntryQuery;
import be.kwakeroni.parameters.basic.client.query.MappedQuery;
import be.kwakeroni.parameters.basic.client.query.RangedQuery;
import be.kwakeroni.parameters.basic.client.query.ValueQuery;
import be.kwakeroni.parameters.client.api.externalize.ExternalizationContext;

/**
 * (C) 2016 Maarten Van Puymbroeck
 */
public interface BasicExternalizer {

    Object externalizeValueQuery(ValueQuery<?> query, ExternalizationContext context);

    Object externalizeEntryQuery(EntryQuery query, ExternalizationContext context);

    Object externalizeMappedQuery(MappedQuery<?, ?, ?> query, ExternalizationContext context);

    Object externalizeRangedQuery(RangedQuery<?, ?, ?> query, ExternalizationContext context);
}
