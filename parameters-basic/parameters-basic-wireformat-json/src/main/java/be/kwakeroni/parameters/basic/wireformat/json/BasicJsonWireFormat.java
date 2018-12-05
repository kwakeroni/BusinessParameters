package be.kwakeroni.parameters.basic.wireformat.json;

import be.kwakeroni.parameters.backend.api.BackendGroup;
import be.kwakeroni.parameters.backend.api.query.BackendWireFormatterContext;
import be.kwakeroni.parameters.basic.backend.query.BasicBackendWireFormatter;
import be.kwakeroni.parameters.basic.client.query.*;
import be.kwakeroni.parameters.client.api.model.Entry;
import be.kwakeroni.parameters.client.api.query.ClientWireFormatterContext;
import org.json.JSONObject;

import java.util.Map;
import java.util.Optional;

/**
 * Created by kwakeroni on 25/09/17.
 */
public class BasicJsonWireFormat implements BasicClientWireFormatter, BasicBackendWireFormatter {

    private static final String TYPE = "type";
    private static final String TYPE_VALUE = "basic.value";
    private static final String TYPE_ENTRY = "basic.entry";
    private static final String TYPE_MAPPED = "basic.mapped";
    private static final String TYPE_RANGED = "basic.ranged";
    private static final String PARAMETER = "parameter";
    private static final String KEY = "key";
    private static final String VALUE = "value";
    private static final String SUBQUERY = "subquery";

    public String getType() {
        return "json";
    }

    @Override
    public <Q> Optional<Q> tryInternalize(BackendGroup<Q> group, Object query, BackendWireFormatterContext context) {
        return Optional.of(query)
                .filter(q -> q instanceof JSONObject)
                .map(q -> (JSONObject) q)
                .filter(q -> q.has(TYPE))
                .flatMap(q -> Optional.ofNullable(tryInternalize0(group, q, context)));
    }

    private <Q> Q tryInternalize0(BackendGroup<Q> group, JSONObject query, BackendWireFormatterContext context) {
        switch (query.getString(TYPE)) {
            case TYPE_VALUE:
                return internalizeValueQuery(query.getString(PARAMETER), group, context);
            case TYPE_ENTRY:
                return internalizeEntryQuery(group, context);
            case TYPE_MAPPED:
                return internalizeMappedQuery(query.getString(KEY), query.get(SUBQUERY), group, context);
            case TYPE_RANGED:
                return internalizeRangedQuery(query.getString(VALUE), query.get(SUBQUERY), group, context);
            default:
                return null;
        }
    }

    @Override
    public JSONObject externalizeValueQuery(ValueQuery<?> query, ClientWireFormatterContext context) {
        return new JSONObject()
                .put(TYPE, TYPE_VALUE)
                .put(PARAMETER, query.getParameter().getName());
    }

    @Override
    public JSONObject externalizeEntryQuery(EntryQuery query, ClientWireFormatterContext context) {
        return new JSONObject()
                .put(TYPE, TYPE_ENTRY);
    }

    @Override
    public JSONObject externalizeMappedQuery(MappedQuery<?, ?, ?> query, ClientWireFormatterContext context) {
        return new JSONObject()
                .put(TYPE, TYPE_MAPPED)
                .put(KEY, query.getKeyString())
                .put(SUBQUERY, query.getSubQuery().externalize(context));
    }

    @Override
    public JSONObject externalizeRangedQuery(RangedQuery<?, ?, ?> query, ClientWireFormatterContext context) {
        return new JSONObject()
                .put(TYPE, TYPE_RANGED)
                .put(VALUE, query.getValueString())
                .put("subquery", query.getSubQuery().externalize(context));
    }

    @Override
    public <T> String clientValueToWire(T value, ValueQuery<T> query, ClientWireFormatterContext context) {
        return (value == null) ? null : query.getParameter().toString(value);
    }

    @Override
    public <T> T wireToClientValue(Object result, ValueQuery<T> query, ClientWireFormatterContext context) {
        return (result == null) ? null : query.getParameter().fromString((String) result);
    }

    @Override
    public String clientEntryToWire(Entry entry, EntryQuery query, ClientWireFormatterContext context) {
        return new JSONObject(entry.toMap()).toString();
    }

    @Override
    public Entry wireToClientEntry(Object result, EntryQuery query, ClientWireFormatterContext context) {
        return new DefaultEntry((new JSONObject((String) result)).toMap());
    }


    @Override
    public String wireToBackendValue(Object value) {
        return (String) value;
    }

    @Override
    public Object backendValueToWire(String value) {
        return value;
    }


    @Override
    public Map<String, String> wireToBackendEntry(Object entry) {
        return new DefaultEntry(((JSONObject) entry).toMap()).toMap();
    }

    @Override
    public Object backendEntryToWire(Map<String, String> entry) {
        return new JSONObject(entry);
    }

}
