package be.kwakeroni.parameters.management.rest;

import be.kwakeroni.parameters.backend.api.BackendEntry;
import be.kwakeroni.parameters.backend.api.BusinessParametersBackend;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.MediaType.TEXT_PLAIN;

/**
 * Created by kwakeroni on 11/10/17.
 */
@Path("/management")
@Consumes(APPLICATION_JSON)
@Produces({APPLICATION_JSON, TEXT_PLAIN})
public class RestParameterManagement {

    Logger LOG = LoggerFactory.getLogger(RestParameterManagement.class);

    private final BusinessParametersBackend<?> backend;
    private final ExceptionMapper mapper = new ExceptionMapper();

    public RestParameterManagement(BusinessParametersBackend<?> backend) {
        this.backend = backend;
    }

    @Path("/groups")
    @GET
    @Produces({APPLICATION_JSON})
    public Response getGroups() {
        JSONArray groups = backend.getGroupNames()
                .stream()
                .map(name -> new JSONObject().put("name", name))
                .collect(toJsonArray());

        JSONObject result = new JSONObject()
                .put("groups", groups);

        return Response.ok(result.toString(), MediaType.APPLICATION_JSON).build();
    }

    @Path("/groups/{group}/entries")
    @GET
    @Produces(APPLICATION_JSON)
    public Response getEntries(@PathParam("group") String groupName) {
        JSONArray array = backend.exportEntries(groupName,
                Collectors.mapping(this::toJsonEntry, toJsonArray()));
        return Response.ok(array.toString(), MediaType.APPLICATION_JSON).build();
    }


    @Path("/groups/{group}/entries")
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

    @Path("/groups/{group}/entries/{id}")
    @PATCH
    public Response update(@PathParam("group") String groupName, @PathParam("id") String id, String entry) {
        try (
                MDC.MDCCloseable mdcFlow = MDC.putCloseable("flow", UUID.randomUUID().toString());
                MDC.MDCCloseable mdcGroup = MDC.putCloseable("group", groupName)) {
            LOG.debug("Update entry {} on {}: {}", id, groupName, entry);

            Objects.requireNonNull(groupName, "groupName");
            Objects.requireNonNull(id, "id");
            Objects.requireNonNull(entry, "entry");
            JSONObject jsonEntry = new JSONObject(entry);

            Map<String, String> map = jsonEntry.toMap().entrySet().stream()
                    .collect(Collectors.toMap(Map.Entry::getKey, e -> (e.getValue() == null) ? null : e.getValue().toString()));
            backend.update(groupName, id, map);
            return Response.ok().build();
        } catch (Exception exc) {
            return responseFor(exc);
        }
    }

    @Path("/groups/{group}/entries/{id}/update")
    @POST
    public Response updateAsPost(@PathParam("group") String groupName, @PathParam("id") String id, String patch) {
        return update(groupName, id, patch);
    }

    private JSONObject toJsonEntry(BackendEntry entry) {
        return new JSONObject()
                .put("id", entry.getId())
                .put("parameters", entry.getParameters());
    }

    private Collector<JSONObject, ?, JSONArray> toJsonArray() {
        return Collector.of(JSONArray::new, JSONArray::put, this::combine);
    }

    private JSONArray combine(JSONArray array1, JSONArray array2) {
        array2.forEach(array1::put);
        return array1;
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
