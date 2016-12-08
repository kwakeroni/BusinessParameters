package be.kwakeroni.parameters.basic.wireformat.raw;

import be.kwakeroni.parameters.basic.client.connector.BasicExternalizer;
import be.kwakeroni.parameters.basic.client.query.EntryQuery;
import be.kwakeroni.parameters.basic.client.query.MappedQuery;
import be.kwakeroni.parameters.basic.client.query.RangedQuery;
import be.kwakeroni.parameters.basic.client.query.ValueQuery;
import be.kwakeroni.parameters.basic.connector.MappedEntrySet;
import be.kwakeroni.parameters.basic.connector.RangedEntrySet;
import be.kwakeroni.parameters.basic.connector.SimpleEntrySet;
import be.kwakeroni.parameters.client.api.externalize.ExternalizationContext;
import be.kwakeroni.parameters.client.connector.EntrySet;
import be.kwakeroni.parameters.client.connector.InternalizationContext;
import be.kwakeroni.parameters.client.connector.QueryInternalizer;

import java.util.Objects;

/**
 * (C) 2016 Maarten Van Puymbroeck
 */
public class BasicRawWireFormat implements BasicExternalizer, QueryInternalizer {

    @Override
    public Object externalizeEntryQuery(EntryQuery query, ExternalizationContext context) {
        return query;
    }

    @Override
    public Object externalizeValueQuery(ValueQuery<?> query, ExternalizationContext context) {
        return query;
    }

    @Override
    public Object externalizeMappedQuery(MappedQuery<?, ?, ?> query, ExternalizationContext context) {
        return query;
    }

    @Override
    public Object externalizeRangedQuery(RangedQuery<?, ?, ?> query, ExternalizationContext context) {
        return query;
    }

    @Override
    public <Q> Q tryInternalize(Object query, EntrySet<Q> entrySet, InternalizationContext<Q> context) {
        if (query instanceof ValueQuery) {
            ValueQuery<?> valueQuery = (ValueQuery<?>) query;
            SimpleEntrySet<Q> simple = entrySet.as(SimpleEntrySet.class);
            return simple.getValueQuery(valueQuery.getParameter().getName());
        } else if (query instanceof EntryQuery) {
            SimpleEntrySet<Q> simple = entrySet.as(SimpleEntrySet.class);
            return simple.getEntryQuery();
        } else if (query instanceof RangedQuery) {
            RangedQuery<?, ?, ?> rangedQuery = (RangedQuery<?, ?, ?>) query;
            RangedEntrySet<Q> ranged = entrySet.as(RangedEntrySet.class);

            Q subQuery = context.internalize(ranged.getSubEntrySet(), rangedQuery.getSubQuery());

            return ranged.getEntryQuery(rangedQuery.getValueString(), subQuery);
        } else if (query instanceof MappedQuery) {
            MappedQuery<?, ?, ?> mappedQuery = (MappedQuery<?, ?, ?>) query;
            MappedEntrySet<Q> mapped = entrySet.as(MappedEntrySet.class);

            Object rawSubQuery = Objects.requireNonNull(mappedQuery.getSubQuery());
            Q subQuery = context.internalize(mapped.getSubEntrySet(), rawSubQuery);

            return mapped.getEntryQuery(mappedQuery.getKeyString(), subQuery);
        }
        return null;
    }
}
