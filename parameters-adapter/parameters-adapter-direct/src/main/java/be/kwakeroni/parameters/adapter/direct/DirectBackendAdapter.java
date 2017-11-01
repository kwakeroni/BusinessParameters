package be.kwakeroni.parameters.adapter.direct;

import be.kwakeroni.parameters.backend.api.BusinessParametersBackend;
import be.kwakeroni.parameters.backend.api.query.BackendQuery;
import be.kwakeroni.parameters.backend.api.query.BackendWireFormatterContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * (C) 2017 Maarten Van Puymbroeck
 */
class DirectBackendAdapter<Q> {

    Logger LOG = LoggerFactory.getLogger(DirectBackendAdapter.class);

    private BusinessParametersBackend<Q> backend;
    private BackendWireFormatterContext wireFormatterContext;

    DirectBackendAdapter(BusinessParametersBackend<Q> backend, BackendWireFormatterContext wireFormatterContext) {
        this.backend = Objects.requireNonNull(backend, "backend");
        this.wireFormatterContext = Objects.requireNonNull(wireFormatterContext, "wireFormatterContext");
    }

    public BusinessParametersBackend<?> getBackend() {
        return this.backend;
    }

    public Collection<String> getGroupNames() {
        return backend.getGroupNames();
    }

    public Object get(String groupName, Object queryObject) {

        try (MDC.MDCCloseable mdcFlow = MDC.putCloseable("flow", UUID.randomUUID().toString());
             MDC.MDCCloseable mdcGroup = MDC.putCloseable("group", groupName)) {
            LOG.debug("Query on {}: {}", groupName, queryObject);

            BackendQuery<? extends Q, ?> query = backend.internalizeQuery(groupName, queryObject, wireFormatterContext);
            Object result = getExternalResult(query, groupName);

            LOG.debug("Returning result: {}", result);
            return result;
        }
    }

    public void set(String groupName, Object queryObject, Object value) {
        try (
                MDC.MDCCloseable mdcFlow = MDC.putCloseable("flow", UUID.randomUUID().toString());
                MDC.MDCCloseable mdcGroup = MDC.putCloseable("group", groupName)) {
            LOG.debug("Write on {}: {} <- {}", groupName, queryObject, value);

            BackendQuery<? extends Q, ?> query = backend.internalizeQuery(groupName, queryObject, wireFormatterContext);
            setInternalResult(query, groupName, value);
        }
    }


    public void addEntry(String groupName, Map<String, String> entry) {
        try (
                MDC.MDCCloseable mdcFlow = MDC.putCloseable("flow", UUID.randomUUID().toString());
                MDC.MDCCloseable mdcGroup = MDC.putCloseable("group", groupName)) {

            LOG.debug("Add entry on {}: {}", groupName, entry);
            backend.insert(groupName, entry);
        }

    }

    private BackendQuery<? extends Q, ?> internalizeQuery(Object query, String groupName) {
        LOG.debug("Internalizing query: {}", query);
        return backend.internalizeQuery(groupName, query, wireFormatterContext);
    }

    private <V> Object getExternalResult(BackendQuery<? extends Q, V> query, String groupName) {
        LOG.debug("Executing query: {}", query);
        V result = backend.select(groupName, query);
        LOG.debug("Externalizing query result: {}", result);
        return query.externalizeValue(result, this.wireFormatterContext);
    }

    private <V> void setInternalResult(BackendQuery<? extends Q, V> query, String groupName, Object valueObject) {
        LOG.debug("Internalizing value to be written: {}", valueObject);

        V value = query.internalizeValue(valueObject, this.wireFormatterContext);
        LOG.debug("Writing value {} to query: {}", value, query);
        backend.update(groupName, query, value);
    }


}
