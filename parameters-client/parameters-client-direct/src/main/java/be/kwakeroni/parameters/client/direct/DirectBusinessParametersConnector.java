package be.kwakeroni.parameters.client.direct;

import be.kwakeroni.parameters.backend.api.BusinessParametersBackend;
import be.kwakeroni.parameters.client.api.BusinessParameters;
import be.kwakeroni.parameters.client.api.model.EntryType;
import be.kwakeroni.parameters.client.api.model.ParameterGroup;
import be.kwakeroni.parameters.client.api.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * (C) 2016 Maarten Van Puymbroeck
 */
public class DirectBusinessParametersConnector implements BusinessParameters {

    private static final Logger LOG = LoggerFactory.getLogger(DirectBusinessParametersConnector.class);

    private final WireFormatterRegistry formatters;
    private final BackendRegistry backends;

    public DirectBusinessParametersConnector(WireFormatterRegistry formatters, BackendRegistry backends) {
        this.formatters = formatters;
        this.backends = backends;
    }

    @Override
    public <ET extends EntryType, T> T get(ParameterGroup<ET> group, Query<ET, T> query) {
        BusinessParametersBackend backend = getBackend(group);
        Object external = externalize(query);
        Object resultObject = executeQuery(backend, group.getName(), external);
        T result = internalize(resultObject, query);
        return result;
    }

    private Object externalize(Query<?, ?> query){
        LOG.debug("Externalizing query {}", query);
        System.out.println("Externalizing query {}" + query);
        return query.externalize(this.formatters);
    }

    private BusinessParametersBackend getBackend(ParameterGroup<?> group){
        LOG.debug("Retrieving api for {}", group.getName());
        System.out.println("Retrieving api for {}" + group.getName());
        return backends.get(group.getName());
    }

    private Object executeQuery(BusinessParametersBackend backend, String groupName, Object external){
        System.out.println("Querying on " + groupName);
        Object result = backend.get(groupName, external);
        System.out.println("Query on " + groupName + " has result " + result);
        return result;
    }

    private <T> T internalize(Object result, Query<?, T> query){
        LOG.debug("Internalizing result {}", query);
        System.out.println("Internalizing result {}" + query);
        return query.internalizeResult(result, this.formatters);
    }

}
