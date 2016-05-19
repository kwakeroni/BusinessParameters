package be.kwakeroni.parameters.client.basic.external;

import be.kwakeroni.parameters.client.api.externalize.ExternalizationContext;
import be.kwakeroni.parameters.client.query.basic.EntryQuery;
import be.kwakeroni.parameters.client.query.basic.MappedQuery;
import be.kwakeroni.parameters.client.query.basic.RangedQuery;
import be.kwakeroni.parameters.client.query.basic.ValueQuery;

/**
 * (C) 2016 Maarten Van Puymbroeck
 */
public interface BasicExternalizer {

    Object externalizeValueQuery(ValueQuery<?> query, ExternalizationContext context);

    Object externalizeEntryQuery(EntryQuery query, ExternalizationContext context);

    Object externalizeMappedQuery(MappedQuery<?, ?, ?> query, ExternalizationContext context);

    Object externalizeRangedQuery(RangedQuery<?, ?, ?> query, ExternalizationContext context);
}
