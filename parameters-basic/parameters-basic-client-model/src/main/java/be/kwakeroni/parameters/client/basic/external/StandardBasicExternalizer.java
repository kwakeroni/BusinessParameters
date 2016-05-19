package be.kwakeroni.parameters.client.basic.external;

import be.kwakeroni.parameters.client.api.externalize.ExternalizationContext;
import be.kwakeroni.parameters.client.query.basic.EntryQuery;
import be.kwakeroni.parameters.client.query.basic.MappedQuery;
import be.kwakeroni.parameters.client.query.basic.RangedQuery;
import be.kwakeroni.parameters.client.query.basic.ValueQuery;

import java.util.HashMap;
import java.util.Map;

/**
 * (C) 2016 Maarten Van Puymbroeck
 */
public class StandardBasicExternalizer implements BasicExternalizer {

    private static final String QUALIFIER = "be.kwakeroni.parameters.client.basic$";
    public static final String TYPE = "type";
    public static final String SUBQUERY = "subquery";
    public static final String PARAMETER = "parameter";
    public static final String VALUE = "value";

    @Override
    public Object externalizeValueQuery(ValueQuery<?> query, ExternalizationContext context) {
        Map<String, Object> map = mapFor(ValueQuery.class);
        map.put(PARAMETER, query.getParameter().getName());
        return map;
    }

    @Override
    public Object externalizeEntryQuery(EntryQuery query, ExternalizationContext context) {
        Map<String, Object> map = mapFor(EntryQuery.class);
        return map;
    }

    @Override
    public Object externalizeMappedQuery(MappedQuery<?, ?, ?> query, ExternalizationContext context) {
        Map<String, Object> map = mapFor(MappedQuery.class);
        map.put(SUBQUERY, query.getSubQuery().externalize(context));
        map.put(VALUE, query.getKeyString());
        return map;
    }

    @Override
    public Object externalizeRangedQuery(RangedQuery<?, ?, ?> query, ExternalizationContext context) {
        Map<String, Object> map = mapFor(RangedQuery.class);
        map.put(SUBQUERY, query.getSubQuery().externalize(context));
        map.put(VALUE, query.getValueString());
        return map;
    }

    private Map<String, Object> mapFor(Class<?> type){
        Map<String, Object> map = new HashMap<>();
        map.put(TYPE, QUALIFIER + type.getSimpleName());
        return map;
    }
}
