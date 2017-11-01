package be.kwakeroni.parameters.adapter.rest;

import be.kwakeroni.parameters.backend.api.BusinessParametersBackend;
import be.kwakeroni.parameters.backend.api.query.BackendQuery;
import be.kwakeroni.parameters.backend.api.query.BackendWireFormatterContext;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.MediaType.TEXT_PLAIN;

/**
 * Created by kwakeroni on 09/10/17.
 */
@Path("/client")
@Consumes(APPLICATION_JSON)
@Produces({APPLICATION_JSON, TEXT_PLAIN})
public class RestBackendAdapter {

    Logger LOG = LoggerFactory.getLogger(RestBackendAdapter.class);

    private final BusinessParametersBackend<?> backend;
    private final BackendWireFormatterContext wireFormatterContext;
    private final ExceptionMapper mapper = new ExceptionMapper();

    public RestBackendAdapter(BusinessParametersBackend<?> backend, BackendWireFormatterContext wireFormatterContext) {
        this.backend = Objects.requireNonNull(backend, "backend");
        this.wireFormatterContext = Objects.requireNonNull(wireFormatterContext, "wireFormatterContext");
    }

    @Path("/{group}/query")
    @POST
    public Response select(@PathParam("group") String groupName, String query) {

        try (MDC.MDCCloseable mdcFlow = MDC.putCloseable("flow", UUID.randomUUID().toString());
             MDC.MDCCloseable mdcGroup = MDC.putCloseable("group", groupName)) {
            LOG.debug("Query on {}: {}", groupName, query);

            Objects.requireNonNull(groupName, "groupName");
            Objects.requireNonNull(query, "query");
            JSONObject jsonQuery = new JSONObject(query);
            Object result = select(groupName, jsonQuery, backend);

            LOG.debug("Returning result: {}", result);
            return (result == null) ? responseForNull() :
                    (result instanceof JSONObject) ? responseFor((JSONObject) result) : responseFor(result.toString());
        } catch (Exception exc) {
            return responseFor(exc);
        }
    }

    private <Q> Object select(String groupName, JSONObject query, BusinessParametersBackend<Q> backend) {
        return select(groupName, backend.internalizeQuery(groupName, query, wireFormatterContext), backend);
    }

    private <Q, V> Object select(String groupName, BackendQuery<? extends Q, V> query, BusinessParametersBackend<Q> backend) {
        V result = backend.select(groupName, query);
        return query.externalizeValue(result, wireFormatterContext);
    }

    @Path("/{group}")
    @PATCH
    public Response update(@PathParam("group") String groupName, String patch) {
        try (
                MDC.MDCCloseable mdcFlow = MDC.putCloseable("flow", UUID.randomUUID().toString());
                MDC.MDCCloseable mdcGroup = MDC.putCloseable("group", groupName)) {
            LOG.debug("Write on {}: {}", groupName, patch);

            Objects.requireNonNull(groupName, "groupName");
            Objects.requireNonNull(patch, "patch");
            JSONObject jsonPatch = new JSONObject(patch);

            JSONObject query = jsonPatch.getJSONObject("query");
            Object value = jsonPatch.get("value");

            update(groupName, query, value, backend);
            return Response.ok().build();
        } catch (Exception exc) {
            return responseFor(exc);
        }
    }

    @Path("/{group}/update")
    @POST
    public Response updateAsPost(@PathParam("group") String groupName, String patch) {
        return update(groupName, patch);
    }

    private <Q> void update(String groupName, JSONObject query, Object value, BusinessParametersBackend<Q> backend) {
        update(groupName, backend.internalizeQuery(groupName, query, wireFormatterContext), value, backend);
    }

    private <Q, V> void update(String groupName, BackendQuery<? extends Q, V> query, Object rawValue, BusinessParametersBackend<Q> backend) {
        V value = query.internalizeValue(rawValue, wireFormatterContext);
        backend.update(groupName, query, value);
    }

    @Path("/{group}")
    @POST
    public Response insert(@PathParam("group") String groupName, String entry) {

        try (
                MDC.MDCCloseable mdcFlow = MDC.putCloseable("flow", UUID.randomUUID().toString());
                MDC.MDCCloseable mdcGroup = MDC.putCloseable("group", groupName)) {
            LOG.debug("Add entry on {}: {}", groupName, entry);

            Objects.requireNonNull(groupName, "groupName");
            Objects.requireNonNull(entry, "entry");
            JSONObject jsonEntry = new JSONObject(entry);

            Map<String, String> map = jsonEntry.toMap().entrySet().stream()
                    .collect(Collectors.toMap(Map.Entry::getKey, e -> (e.getValue() == null) ? null : e.getValue().toString()));
            backend.insert(groupName, map);
            return Response.ok().build();
        } catch (Exception exc) {
            return responseFor(exc);
        }

    }

    private <R> R responseFor(Exception exc) {
        WebApplicationException webEx = mapper.mapToRestException(exc);
        LOG.error("Returning Rest status " + webEx.getResponse().getStatus(), exc);
        throw webEx;
    }

    private Response responseFor(JSONObject json) {
        return Response.ok(json.toString(), APPLICATION_JSON).build();
    }

    private Response responseFor(String string) {
        return Response.ok(string, TEXT_PLAIN).build();
    }

    private Response responseForNull() {
        return Response.noContent().build();
    }

}
