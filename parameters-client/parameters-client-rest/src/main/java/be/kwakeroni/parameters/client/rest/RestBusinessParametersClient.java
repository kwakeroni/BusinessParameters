package be.kwakeroni.parameters.client.rest;

import be.kwakeroni.parameters.client.api.WritableBusinessParameters;
import be.kwakeroni.parameters.client.api.model.Entry;
import be.kwakeroni.parameters.client.api.model.EntryType;
import be.kwakeroni.parameters.client.api.model.ParameterGroup;
import be.kwakeroni.parameters.client.api.query.ClientWireFormatterContext;
import be.kwakeroni.parameters.client.api.query.Query;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;
import java.util.Optional;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

public class RestBusinessParametersClient implements WritableBusinessParameters {

    private static Logger LOG = LoggerFactory.getLogger(RestBusinessParametersClient.class);

    private final ClientWireFormatterContext context;
    private Client client;
    private WebResource resource;

    public RestBusinessParametersClient(String url, ClientWireFormatterContext context) {
        this.context = context;
        this.client = Client.create();
        this.resource = this.client.resource(normalizePath(url));
    }

    private String normalizePath(String path) {
        if (!path.endsWith("/")) {
            path = path + "/";
        }
        return path;
    }

    private WebResource resource(String path) {
        return this.resource.path(path);
    }


    @Override
    public <ET extends EntryType, T> Optional<T> get(ParameterGroup<ET> group, Query<ET, T> query) {
        Objects.requireNonNull(group, "group");
        Objects.requireNonNull(group.getName(), "group.name");
        Objects.requireNonNull(query, "query");

        String external = externalize(query);

        WebResource resource = resource("/" + group.getName() + "/query");
        ClientResponse response = resource.type(APPLICATION_JSON).post(ClientResponse.class, external);
        Optional<T> result = process(resource, response, String.class)
                .flatMap(extValue -> internalizeResult(query, extValue));

        LOG.info("\n    Query  " + external + "\n    Result " + result);
        return result;
    }

    @Override
    public <ET extends EntryType, T> void set(ParameterGroup<ET> group, Query<ET, T> query, T value) {
        Objects.requireNonNull(group, "group");
        Objects.requireNonNull(group.getName(), "group.name");
        Objects.requireNonNull(query, "query");
        Objects.requireNonNull(value, "value");

        JSONObject request = new JSONObject()
                .put("query", externalize(query))
                .put("value", externalizeValue(query, value));


        WebResource resource = resource("/" + group.getName() + "/update");
        ClientResponse response = resource.type(APPLICATION_JSON).post(ClientResponse.class, request.toString());
        process(resource, response);
    }

    @Override
    public void addEntry(ParameterGroup<?> group, Entry entry) {
        Objects.requireNonNull(group, "group");
        Objects.requireNonNull(group.getName(), "group.name");
        Objects.requireNonNull(entry, "entry");

        JSONObject request = new JSONObject(entry.toMap());

        WebResource resource = resource("/" + group.getName());
        ClientResponse response = resource.type(APPLICATION_JSON).post(ClientResponse.class, request.toString());
        process(resource, response);
    }

    private String externalize(Query<?, ?> query) {
        Object external = query.externalize(this.context);
        if (external == null) {
            throw new IllegalArgumentException("Wireformat produced null for " + query);
        }
        if (external instanceof JSONObject) {
            external = ((JSONObject) external).toString();
        }
        if (!(external instanceof String)) {
            throw new IllegalArgumentException("Wireformat produced " + external.getClass().getName() + " instead of String for " + query);
        }
        return (String) external;
    }

    private <T> Object externalizeValue(Query<?, T> query, T value) {
        return query.externalizeValue(value, this.context);
    }

    private <T> Optional<T> internalizeResult(Query<?, T> query, String value) {
        return query.internalizeResult(value, this.context);
    }

    private <T> Optional<T> process(WebResource resource, ClientResponse response, Class<T> type) {
        return process(resource.getURI().toString(), response, type);
    }

    private <T> Optional<T> process(String url, ClientResponse response, Class<T> type) {
        return getEntity(process(url, response), type);
    }

    private ClientResponse process(WebResource resource, ClientResponse response) {
        return process(resource.getURI().toString(), response);
    }

    private ClientResponse process(String url, ClientResponse response) {
        switch (response.getStatusInfo().getFamily()) {
            case CLIENT_ERROR:
                throw new IllegalArgumentException("Error response from Business Parameters REST service: url=" + url + " status=" + response.getStatus() + " message=" + response.getEntity(String.class));
            case SERVER_ERROR:
                throw new IllegalStateException("Error response from Business Parameters REST service: url=" + url + " status=" + response.getStatus() + " message=" + response.getEntity(String.class));
            case OTHER:
                throw new IllegalStateException("Unknown response from Business Parameters REST service: url=" + url + " status=" + response.getStatus() + " message=" + response.getEntity(String.class));
            case INFORMATIONAL:
            case REDIRECTION:
                LOG.warn("Unexpected response: url=" + url + " status=" + response.getStatus() + " entity=" + response.getEntity(String.class));
            case SUCCESSFUL:
                return response;
        }
        return response;
    }

    private <T> Optional<T> getEntity(ClientResponse response, Class<T> type) {
        return Optional.of(response)
                .filter(r -> r.getStatus() != 204)
                .map(r -> r.getEntity(type));
    }
}
