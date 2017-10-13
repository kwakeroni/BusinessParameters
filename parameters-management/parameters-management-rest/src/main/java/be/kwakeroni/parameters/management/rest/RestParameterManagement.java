package be.kwakeroni.parameters.management.rest;

import be.kwakeroni.parameters.backend.api.BusinessParametersBackend;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

/**
 * Created by kwakeroni on 11/10/17.
 */
@Path("/management")
@Consumes(APPLICATION_JSON)
@Produces({APPLICATION_JSON})
public class RestParameterManagement {

    Logger LOG = LoggerFactory.getLogger(RestParameterManagement.class);

    private final BusinessParametersBackend<?> backend;

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
                Collectors.mapping(JSONObject::new, toJsonArray()));
        return Response.ok(array.toString(), MediaType.APPLICATION_JSON).build();
    }

    private Collector<JSONObject, ?, JSONArray> toJsonArray() {
        return Collector.of(JSONArray::new, JSONArray::put, this::combine);
    }

    private JSONArray combine(JSONArray array1, JSONArray array2) {
        array2.forEach(array1::put);
        return array1;
    }

}
