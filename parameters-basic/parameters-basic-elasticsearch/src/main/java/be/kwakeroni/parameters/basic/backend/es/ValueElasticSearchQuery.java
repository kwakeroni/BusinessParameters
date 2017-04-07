package be.kwakeroni.parameters.basic.backend.es;

import be.kwakeroni.parameters.backend.api.query.BackendWireFormatterContext;
import be.kwakeroni.parameters.backend.es.api.ElasticSearchData;
import be.kwakeroni.parameters.backend.es.api.ElasticSearchEntry;
import be.kwakeroni.parameters.backend.es.api.ElasticSearchQuery;
import be.kwakeroni.parameters.backend.es.api.EntryModification;
import be.kwakeroni.parameters.basic.backend.query.BasicBackendWireFormatter;
import org.json.JSONObject;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * (C) 2017 Maarten Van Puymbroeck
 */
class ValueElasticSearchQuery implements ElasticSearchQuery<String> {

    private final String parameterName;

    ValueElasticSearchQuery(String parameterName) {
        this.parameterName = parameterName;
    }

    @Override
    public Optional<String> apply(ElasticSearchData data) {
        return getEntryFrom(data)
                .map(jo -> jo.getParameter(this.parameterName));
    }

    @Override
    public EntryModification getEntryModification(String value, ElasticSearchData data) {
        return getEntryFrom(data)
                .map(EntryModification.modifiedBy(e -> e.setParameter(parameterName, value)))
                .orElseThrow(() -> new IllegalArgumentException("Not found entry to change"));
    }

    private Optional<ElasticSearchEntry> getEntryFrom(ElasticSearchData data) {
        return data.findAll(2)
                .reduce(ElasticSearchSimpleGroup.atMostOne());
    }

    @Override
    public Object externalizeValue(String value, BackendWireFormatterContext wireFormatterContext) {
        return wireFormatterContext.getWireFormatter(BasicBackendWireFormatter.class).backendValueToWire(value);
    }

    @Override
    public String internalizeValue(Object value, BackendWireFormatterContext wireFormatterContext) {
        return wireFormatterContext.getWireFormatter(BasicBackendWireFormatter.class).wireToBackendValue(value);
    }
}
