package be.kwakeroni.parameters.basic.wireformat.standard;

import be.kwakeroni.parameters.basic.client.connector.BasicExternalizer;
import be.kwakeroni.parameters.basic.client.query.EntryQuery;
import be.kwakeroni.parameters.basic.client.query.MappedQuery;
import be.kwakeroni.parameters.basic.client.query.RangedQuery;
import be.kwakeroni.parameters.basic.client.query.ValueQuery;
import be.kwakeroni.parameters.basic.connector.SimpleEntrySet;
import be.kwakeroni.parameters.client.api.externalize.ExternalizationContext;
import be.kwakeroni.parameters.client.connector.EntrySet;
import be.kwakeroni.parameters.client.connector.InternalizationContext;
import be.kwakeroni.parameters.client.connector.QueryInternalizer;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * (C) 2016 Maarten Van Puymbroeck
 */
public class BasicStandardWireformat implements BasicExternalizer, QueryInternalizer {

    public static final String TYPE = "type";
    public static final String SUBQUERY = "subquery";
    public static final String PARAMETER = "parameter";
    public static final String VALUE = "value";

    public BasicStandardWireformat(){

    }

    @Override
    public Object externalizeValueQuery(ValueQuery<?> query, ExternalizationContext context) {
        Map<String, Object> map = mapFor(ExternalQuery.VALUE);
        map.put(PARAMETER, query.getParameter().getName());
        return map;
    }

    public <Q> Q internalize(Map<String, Object> rawQuery, EntrySet<Q> entrySet, InternalizationContext<Q> context) {

        SimpleEntrySet<Q> simple = entrySet.as(SimpleEntrySet.class);

        String parameterName = (String) Objects.requireNonNull(rawQuery.get(PARAMETER));

        return simple.getValueQuery(parameterName);
    }


    @Override
    public Object externalizeEntryQuery(EntryQuery query, ExternalizationContext context) {
        Map<String, Object> map = mapFor(ExternalQuery.ENTRY);
        return map;
    }

    @Override
    public Object externalizeMappedQuery(MappedQuery<?, ?, ?> query, ExternalizationContext context) {
        Map<String, Object> map = mapFor(ExternalQuery.MAPPED);
        map.put(SUBQUERY, query.getSubQuery().externalize(context));
        map.put(VALUE, query.getKeyString());
        return map;
    }

    @Override
    public Object externalizeRangedQuery(RangedQuery<?, ?, ?> query, ExternalizationContext context) {
        Map<String, Object> map = mapFor(ExternalQuery.RANGED);
        map.put(SUBQUERY, query.getSubQuery().externalize(context));
        map.put(VALUE, query.getValueString());
        return map;
    }

    private Map<String, Object> mapFor(ExternalQuery type) {
        Map<String, Object> map = new HashMap<>();
        map.put(BasicStandardWireformat.TYPE, type.getDiscriminator());
        return map;
    }


    @Override
    public <Q> Q tryInternalize(Object query, EntrySet<Q> entrySet, InternalizationContext<Q> context) {
        if (query instanceof Map) {
            Object type = ((Map<?, ?>) query).get(TYPE);
            for (ExternalQuery queryType : ExternalQuery.values()) {
                if (type.equals(queryType.getDiscriminator())) {
                    return queryType.internalize((Map<String, Object>) query, entrySet, context);
                }
            }
        }
        return null;
    }

}
