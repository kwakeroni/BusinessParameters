package be.kwakeroni.parameters.basic.backend.es;

import be.kwakeroni.parameters.backend.api.query.BackendWireFormatterContext;
import be.kwakeroni.parameters.backend.es.api.ElasticSearchCriteria;
import be.kwakeroni.parameters.backend.es.api.ElasticSearchData;
import be.kwakeroni.parameters.backend.es.api.ElasticSearchQuery;
import be.kwakeroni.parameters.basic.backend.query.BasicBackendWireFormatter;

import java.util.Optional;

/**
 * (C) 2017 Maarten Van Puymbroeck
 */
class ValueElasticSearchQuery implements ElasticSearchQuery<String> {

    private final String parameterName;

    ValueElasticSearchQuery(String parameterName) {
        this.parameterName = parameterName;
    }

    @Override
    public Optional<String> apply(ElasticSearchData data, ElasticSearchCriteria criteria) {
        return data.query(criteria, 2)
                .reduce(ElasticSearchSimpleGroup.atMostOne())
                .map(jo -> jo.getString(this.parameterName));
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
