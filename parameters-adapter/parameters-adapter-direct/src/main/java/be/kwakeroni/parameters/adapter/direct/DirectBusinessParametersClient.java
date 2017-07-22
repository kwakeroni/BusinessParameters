package be.kwakeroni.parameters.adapter.direct;

import be.kwakeroni.parameters.backend.api.BusinessParametersBackend;
import be.kwakeroni.parameters.backend.api.query.BackendWireFormatterContext;
import be.kwakeroni.parameters.client.api.WritableBusinessParameters;
import be.kwakeroni.parameters.client.api.model.Entry;
import be.kwakeroni.parameters.client.api.model.EntryType;
import be.kwakeroni.parameters.client.api.model.ParameterGroup;
import be.kwakeroni.parameters.client.api.query.ClientWireFormatterContext;
import be.kwakeroni.parameters.client.api.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;
import java.util.Optional;

/**
 * (C) 2016 Maarten Van Puymbroeck
 */
public class DirectBusinessParametersClient implements WritableBusinessParameters {

    private static final Logger LOG = LoggerFactory.getLogger(DirectBusinessParametersClient.class);

    private final DirectBackendAdapter<?> backend;
    private final ClientWireFormatterContext formatters;

    public DirectBusinessParametersClient(BusinessParametersBackend<?> backend, BackendWireFormatterContext backendFormatters, ClientWireFormatterContext clientFormatters) {
        Objects.requireNonNull(backendFormatters, "backendFormatters");
        Objects.requireNonNull(clientFormatters, "clientFormatters");

        this.backend = new DirectBackendAdapter<>(backend, backendFormatters);
        this.formatters = clientFormatters;
    }

    @Override
    public <ET extends EntryType, T> Optional<T> get(ParameterGroup<ET> group, Query<ET, T> query) {
        Object external = externalize(query);
        Object resultObject = executeQuery(backend, group.getName(), external);
        Optional<T> result = internalize(resultObject, query);
        return result;
    }

    @Override
    public <ET extends EntryType, T> void set(ParameterGroup<ET> group, Query<ET, T> query, T value) {
        Object externalQuery = externalize(query);
        Object externalValue = externalizeValue(value, query);
        executeWrite(backend, group.getName(), externalQuery, externalValue);
    }

    @Override
    public void addEntry(ParameterGroup<?> group, Entry entry) {
        backend.addEntry(group.getName(), entry.toMap());
    }

    private Object externalize(Query<?, ?> query) {
        LOG.debug("Externalizing query: {}", query);
        return query.externalize(this.formatters);
    }

    private <T> Object externalizeValue(T value, Query<?, T> query) {
        LOG.debug("Externalizing value: {}", value);
        return query.externalizeValue(value, this.formatters);
    }

    private Object executeQuery(DirectBackendAdapter<?> backend, String groupName, Object external) {
        LOG.debug("Querying on: {}", groupName);
        Object result = backend.get(groupName, external);
        LOG.debug("Query has result: {} ", result);
        return result;
    }

    private void executeWrite(DirectBackendAdapter<?> backend, String groupName, Object externalQuery, Object externalValue) {
        LOG.debug("Writing to: {}", groupName);
        backend.set(groupName, externalQuery, externalValue);
    }

    private <T> Optional<T> internalize(Object result, Query<?, T> query) {
        LOG.debug("Internalizing result: {}", query);
        return query.internalizeResult(result, this.formatters);
    }

}
