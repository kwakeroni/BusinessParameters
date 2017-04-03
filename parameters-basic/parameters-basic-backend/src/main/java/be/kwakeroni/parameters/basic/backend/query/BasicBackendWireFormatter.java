package be.kwakeroni.parameters.basic.backend.query;


import be.kwakeroni.parameters.backend.api.BackendGroup;
import be.kwakeroni.parameters.backend.api.query.BackendWireFormatter;
import be.kwakeroni.parameters.backend.api.query.BackendWireFormatterContext;

import java.util.Map;

/**
 * (C) 2016 Maarten Van Puymbroeck
 */
public interface BasicBackendWireFormatter extends BackendWireFormatter {

    String wireToBackendValue(Object value);

    Object backendValueToWire(String value);

    Map<String, String> wireToBackendEntry(Object entry);

    Object backendEntryToWire(Map<String, String> entry);

    default <Q> Q internalizeValueQuery(String parameter, BackendGroup<Q> group, BackendWireFormatterContext context) {
        SimpleBackendGroup<Q> simple = group.as(SimpleBackendGroup.class);
        return simple.getValueQuery(parameter);
    }

    default <Q> Q internalizeEntryQuery(BackendGroup<Q> group, BackendWireFormatterContext context) {
        SimpleBackendGroup<Q> simple = group.as(SimpleBackendGroup.class);
        return simple.getEntryQuery();
    }

    default <Q> Q internalizeRangedQuery(String value, Object rawSubQuery, BackendGroup<Q> group, BackendWireFormatterContext context) {
        RangedBackendGroup<Q, ?> ranged = group.as(RangedBackendGroup.class);
        Q subQuery = context.internalize(ranged.getSubGroup(), rawSubQuery);
        return ranged.getEntryQuery(value, subQuery);
    }

    default <Q> Q internalizeMappedQuery(String key, Object rawSubQuery, BackendGroup<Q> group, BackendWireFormatterContext context) {
        MappedBackendGroup<Q, ?> mapped = group.as(MappedBackendGroup.class);
        Q subQuery = context.internalize(mapped.getSubGroup(), rawSubQuery);
        return mapped.getEntryQuery(key, subQuery);
    }


}
