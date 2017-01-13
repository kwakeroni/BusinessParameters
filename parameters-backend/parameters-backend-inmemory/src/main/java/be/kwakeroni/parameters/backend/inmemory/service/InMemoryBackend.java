package be.kwakeroni.parameters.backend.inmemory.service;

import be.kwakeroni.parameters.backend.api.BusinessParametersBackend;
import be.kwakeroni.parameters.backend.api.query.BackendWireFormatterContext;
import be.kwakeroni.parameters.backend.inmemory.api.GroupData;
import be.kwakeroni.parameters.backend.inmemory.api.InMemoryQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * (C) 2016 Maarten Van Puymbroeck
 */
public class InMemoryBackend implements BusinessParametersBackend {

    Logger LOG = LoggerFactory.getLogger(InMemoryBackend.class);

    private final Map<String, GroupData> data;
    private final BackendWireFormatterContext<InMemoryQuery<?>> wireFormatterContext;

    public InMemoryBackend(BackendWireFormatterContext<InMemoryQuery<?>> context) {
        this(new HashMap<>(), context);
    }

    private InMemoryBackend(Map<String, GroupData> data, BackendWireFormatterContext<InMemoryQuery<?>> context) {
        this.data = data;
        this.wireFormatterContext = context;
    }

    public void setGroupData(String groupName, GroupData data) {
        this.data.put(groupName, data);
    }

    public void addGroupData(String groupName, GroupData data) {
        this.data.merge(groupName, data, (key, d) -> {
            throw new IllegalStateException("Duplicate data for group " + key);
        });
    }

    @Override
    public Object get(String group, Object queryObject) {

        try (
                MDC.MDCCloseable mdcFlow = MDC.putCloseable("flow", UUID.randomUUID().toString());
                MDC.MDCCloseable mdcGroup = MDC.putCloseable("group", group)) {
            LOG.debug("Query on {}: {}", group, queryObject);
            GroupData groupData = getGroupData(group);
            LOG.debug("Internalizing query: {}", queryObject);
            InMemoryQuery<?> query = wireFormatterContext.internalize(groupData.getGroup(), queryObject);
            Object result = getExternalResult(query, group, groupData);
            LOG.debug("Returning result: {}", result);
            return result;
        }
    }

    private <T> Object getExternalResult(InMemoryQuery<T> query, String group, GroupData groupData) {
        LOG.debug("Executing query: {}", query);
        T result = query.apply(groupData.getEntries()).orElse(null);
        LOG.debug("Externalizing query result: {}", result);
        return query.externalizeResult(result, this.wireFormatterContext);
    }

    private GroupData getGroupData(String name) {
        return Optional.ofNullable(data.get(name))
                .orElseThrow(() -> new IllegalArgumentException("No group defined with name " + name));
    }

    @Override
    public Collection<String> getGroupNames() {
        return data.keySet();
    }
}
