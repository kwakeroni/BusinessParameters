package be.kwakeroni.parameters.basic.backend.es;

import be.kwakeroni.parameters.backend.api.query.BackendWireFormatterContext;
import be.kwakeroni.parameters.backend.es.api.ElasticSearchCriteria;
import be.kwakeroni.parameters.backend.es.api.ElasticSearchData;
import be.kwakeroni.parameters.backend.es.api.ElasticSearchQuery;
import be.kwakeroni.parameters.basic.backend.query.BasicBackendWireFormatter;
import org.json.JSONObject;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.*;

/**
 * (C) 2017 Maarten Van Puymbroeck
 */
class EntryElasticSearchQuery implements ElasticSearchQuery<Map<String, String>> {

    static final EntryElasticSearchQuery INSTANCE = new EntryElasticSearchQuery();

    private EntryElasticSearchQuery() {

    }

    @Override
    public Optional<Map<String, String>> apply(ElasticSearchData data, ElasticSearchCriteria criteria) {
        return data.query(criteria, 2)
                .reduce(ElasticSearchSimpleGroup.atMostOne())
                .map(this::toStringMap);
    }

    private Map<String, String> toStringMap(JSONObject jo) {
        return jo.keySet()
                .stream()
                .collect(collectingAndThen(toMap(identity(), jo::getString), Collections::unmodifiableMap));
    }

    @Override
    public Object externalizeValue(Map<String, String> entry, BackendWireFormatterContext wireFormatterContext) {
        return wireFormatterContext.getWireFormatter(BasicBackendWireFormatter.class).backendEntryToWire(entry);
    }

    @Override
    public Map<String, String> internalizeValue(Object entry, BackendWireFormatterContext wireFormatterContext) {
        return wireFormatterContext.getWireFormatter(BasicBackendWireFormatter.class).wireToBackendEntry(entry);
    }
}
