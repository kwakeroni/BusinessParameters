package be.kwakeroni.parameters.adapter.direct;

import be.kwakeroni.parameters.backend.api.BackendGroup;
import be.kwakeroni.parameters.backend.api.BusinessParametersBackend;
import be.kwakeroni.parameters.backend.api.query.BackendQuery;
import be.kwakeroni.parameters.backend.api.query.BackendWireFormatterContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;

/**
 * (C) 2017 Maarten Van Puymbroeck
 */
class DirectBackendAdapter {

    Logger LOG = LoggerFactory.getLogger(DirectBackendAdapter.class);

    private BusinessParametersBackend<?,?,?> backend;
    private BackendWireFormatterContext wireFormatterContext;

    DirectBackendAdapter(BusinessParametersBackend<?,?,?> backend, BackendWireFormatterContext wireFormatterContext) {
        this.backend = backend;
        this.wireFormatterContext = wireFormatterContext;
    }

    public BusinessParametersBackend<?,?,?> getBackend(){
        return this.backend;
    }

    public Collection<String> getGroupNames() {
        return backend.getGroupNames();
    }

    public Object get(String group, Object queryObject) {

        try (
                MDC.MDCCloseable mdcFlow = MDC.putCloseable("flow", UUID.randomUUID().toString());
                MDC.MDCCloseable mdcGroup = MDC.putCloseable("group", group)) {
            LOG.debug("Query on {}: {}", group, queryObject);

            Object result = get(this.backend, group, queryObject);

            LOG.debug("Returning result: {}", result);
            return result;
        }
    }

    private <Q,S,E> Object get(BusinessParametersBackend<Q,S,E> backend, String groupName, Object queryObject) {
        BackendGroup<Q,S,E> group = backend.getGroup(groupName);
        BackendQuery<? extends Q, ?> query = internalizeQuery(queryObject, group);
        return getExternalResult(query, group, backend);
    }

    public void set(String group, Object queryObject, Object value) {
        try (
                MDC.MDCCloseable mdcFlow = MDC.putCloseable("flow", UUID.randomUUID().toString());
                MDC.MDCCloseable mdcGroup = MDC.putCloseable("group", group)) {
            LOG.debug("Write on {}: {} <- {}", group, queryObject, value);
            set(this.backend, group, queryObject, value);
        }
    }

    private <Q,S,E> void set(BusinessParametersBackend<Q,S,E> backend, String groupName, Object queryObject, Object value) {
        BackendGroup<Q, S, E> group = backend.getGroup(groupName);
        BackendQuery<? extends Q, ?> query = internalizeQuery(queryObject, group);
        setInternalResult(query, group, value, backend);
    }


    public void addEntry(String groupName, Map<String, String> entry) {
        try (
                MDC.MDCCloseable mdcFlow = MDC.putCloseable("flow", UUID.randomUUID().toString());
                MDC.MDCCloseable mdcGroup = MDC.putCloseable("group", groupName)) {

            LOG.debug("Add entry on {}: {} <- {}", groupName, entry);
            addEntry(this.backend, groupName, entry);
        }

    }

    private <Q, S, E> void addEntry(BusinessParametersBackend<Q,S,E> backend, String groupName, Map<String, String> entry) {
        BackendGroup<Q, S, E> group = backend.getGroup(groupName);
        backend.insert(group, entry);
    }

    private <Q> BackendQuery<? extends Q, ?> internalizeQuery(Object query, BackendGroup<Q, ?, ?> group) {
        LOG.debug("Internalizing query: {}", query);
        return group.internalize(query, wireFormatterContext);
    }

    private <Q, S, E, V> Object getExternalResult(BackendQuery<? extends Q, V> query, BackendGroup<Q, S, E> group, BusinessParametersBackend<Q,S,E> backend) {
        LOG.debug("Executing query: {}", query);
        V result = backend.select(group, query);
        LOG.debug("Externalizing query result: {}", result);
        return query.externalizeValue(result, this.wireFormatterContext);
    }

    private <Q, S, E, V> void setInternalResult(BackendQuery<? extends Q, V> query, BackendGroup<Q, S, E> group, Object valueObject, BusinessParametersBackend<Q,S,E> backend) {
        LOG.debug("Internalizing value to be written: {}", valueObject);

        V value = query.internalizeValue(valueObject, this.wireFormatterContext);
        LOG.debug("Writing value {} to query: {}", value, query);
        backend.update(group, query, value);
    }


}
