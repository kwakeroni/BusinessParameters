package be.kwakeroni.parameters.basic.backend.es;

import be.kwakeroni.parameters.backend.api.query.BackendWireFormatterContext;
import be.kwakeroni.parameters.backend.es.api.ElasticSearchData;
import be.kwakeroni.parameters.backend.es.api.ElasticSearchEntry;
import be.kwakeroni.parameters.backend.es.api.ElasticSearchQuery;
import be.kwakeroni.parameters.backend.es.api.EntryModification;
import be.kwakeroni.parameters.basic.backend.query.BasicBackendWireFormatter;

import java.util.Map;
import java.util.Optional;

/**
 * (C) 2017 Maarten Van Puymbroeck
 */
class EntryElasticSearchQuery implements ElasticSearchQuery<Map<String, String>> {

    static final EntryElasticSearchQuery INSTANCE = new EntryElasticSearchQuery();

    private EntryElasticSearchQuery() {

    }

    @Override
    public Optional<Map<String, String>> apply(ElasticSearchData data) {
        return data.findAll(2)
                .reduce(ElasticSearchSimpleGroup.atMostOne())
                .map(ElasticSearchEntry::toParameterMap);
    }


    @Override
    public Object externalizeValue(Map<String, String> entry, BackendWireFormatterContext wireFormatterContext) {
        return wireFormatterContext.getWireFormatter(BasicBackendWireFormatter.class).backendEntryToWire(entry);
    }

    @Override
    public Map<String, String> internalizeValue(Object entry, BackendWireFormatterContext wireFormatterContext) {
        return wireFormatterContext.getWireFormatter(BasicBackendWireFormatter.class).wireToBackendEntry(entry);
    }

    @Override
    public EntryModification getEntryModification(Map<String, String> value, ElasticSearchData data) {
        return getEntryFrom(data)
                .map(EntryModification.modifiedBy(e -> {
                    value.forEach(e::setParameter);
                }))
                .orElseThrow(() -> new IllegalArgumentException("Not found entry to change"));
    }

    private Optional<ElasticSearchEntry> getEntryFrom(ElasticSearchData data) {
        return data.findAll(2)
                .reduce(ElasticSearchSimpleGroup.atMostOne());
    }
}
