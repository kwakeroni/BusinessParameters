package be.kwakeroni.parameters.basic.definition.jmx;

import be.kwakeroni.parameters.adapter.jmx.api.JMXInvocation;
import be.kwakeroni.parameters.backend.api.BackendGroup;
import be.kwakeroni.parameters.backend.api.query.BackendWireFormatterContext;
import be.kwakeroni.parameters.basic.backend.query.BasicBackendWireFormatter;
import org.json.JSONObject;

import java.util.Map;
import java.util.Optional;

/**
 * Created by kwakeroni on 11/05/17.
 */
public class BasicJMXBackendWireFormatter implements BasicBackendWireFormatter {

    static final String ACTION_TYPE_VALUE = "basic.value";
    static final String ACTION_TYPE_ENTRY = "basic.entry";
    static final String ACTION_TYPE_RANGED = "basic.ranged";
    static final String ACTION_TYPE_MAPPED = "basic.mapped";

    @Override
    public <Q> Optional<Q> tryInternalize(BackendGroup<Q> group, Object actionObject, BackendWireFormatterContext context) {

        if (actionObject instanceof JMXInvocation) {

            JMXInvocation action = (JMXInvocation) actionObject;

            switch (action.getOperationType()) {
                case ACTION_TYPE_VALUE:
                    return Optional.of(internalizeValueQuery(action.popParameter(), group, context));
                case ACTION_TYPE_ENTRY:
                    return Optional.of(internalizeEntryQuery(group, context));
                case ACTION_TYPE_RANGED:
                    return Optional.of(internalizeRangedQuery(action.popParameter(), action.pop(), group, context));
                case ACTION_TYPE_MAPPED:
                    return Optional.of(internalizeMappedQuery(action.popParameter(), action.pop(), group, context));
            }
        }

        return Optional.empty();
    }

    @Override
    public String wireToBackendValue(Object value) {
        return (value == null) ? null : String.valueOf(value);
    }

    @Override
    public Object backendValueToWire(String value) {
        return value;
    }

    @Override
    public Map<String, String> wireToBackendEntry(Object entry) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object backendEntryToWire(Map<String, String> entry) {
        return new JSONObject(entry).toString(2);
    }
}
