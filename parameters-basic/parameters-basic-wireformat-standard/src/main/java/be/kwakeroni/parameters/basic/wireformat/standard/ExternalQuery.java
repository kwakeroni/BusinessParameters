package be.kwakeroni.parameters.basic.wireformat.standard;

import be.kwakeroni.parameters.basic.connector.MappedEntrySet;
import be.kwakeroni.parameters.basic.connector.RangedEntrySet;
import be.kwakeroni.parameters.basic.connector.SimpleEntrySet;
import be.kwakeroni.parameters.client.connector.EntrySet;
import be.kwakeroni.parameters.client.connector.InternalizationContext;

import java.util.Map;
import java.util.Objects;

import static be.kwakeroni.parameters.basic.wireformat.standard.BasicStandardWireformat.*;

/**
 * (C) 2016 Maarten Van Puymbroeck
 */
public enum ExternalQuery {

    VALUE {
        @Override
        public <Q> Q internalize(Map<String, Object> rawQuery, EntrySet<Q> entrySet, InternalizationContext<Q> context) {
            SimpleEntrySet<Q> simple = entrySet.as(SimpleEntrySet.class);

            String parameterName = (String) Objects.requireNonNull(rawQuery.get(PARAMETER));

            return simple.getValueQuery(parameterName);
        }
    },
    ENTRY {
        @Override
        public <Q> Q internalize(Map<String, Object> rawQuery, EntrySet<Q> entrySet, InternalizationContext<Q> context) {
            SimpleEntrySet<Q> simple = entrySet.as(SimpleEntrySet.class);
            return simple.getEntryQuery();
        }
    },
    RANGED {
        @Override
        public <Q> Q internalize(Map<String, Object> rawQuery, EntrySet<Q> entrySet, InternalizationContext<Q> context) {
            RangedEntrySet<Q> ranged = entrySet.as(RangedEntrySet.class);

            Object rawSubQuery = Objects.requireNonNull(rawQuery.get(SUBQUERY));
            String rawValue = (String) Objects.requireNonNull(rawQuery.get(BasicStandardWireformat.VALUE));
            Q subQuery = context.internalize(ranged.getSubEntrySet(), rawSubQuery);

            return ranged.getEntryQuery(rawValue, subQuery);
        }
    },
    MAPPED {
        @Override
        public <Q> Q internalize(Map<String, Object> rawQuery, EntrySet<Q> entrySet, InternalizationContext<Q> context) {

            MappedEntrySet<Q> mapped = entrySet.as(MappedEntrySet.class);

            Object rawSubQuery = Objects.requireNonNull(rawQuery.get(SUBQUERY));
            String rawValue = (String) Objects.requireNonNull(rawQuery.get(BasicStandardWireformat.VALUE));
            Q subQuery = context.internalize(mapped.getSubEntrySet(), rawSubQuery);

            return mapped.getEntryQuery(rawValue, subQuery);
        }
    };

    public final String getDiscriminator() {
        return "be.kwakeroni.parameters.client.basic$" + name();
    }

    public abstract <Q> Q internalize(Map<String, Object> rawQuery, EntrySet<Q> entrySet, InternalizationContext<Q> context);
}
