package be.kwakeroni.parameters.adapter.rest;

import org.json.JSONException;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Exception table
 * <table border="1">
 * <thead>
 * <tr>
 * <th>Status</th>
 * <th>Exception</th>
 * </tr>
 * </thead>
 * <tbody>
 * <tr>
 * <td>3??</td>
 * <td>RedirectionException</td>
 * </tr>
 * <tr>
 * <td>400</td>
 * <td>BadRequestException<br />ParamException</td>
 * </tr>
 * <tr>
 * <td>401</td>
 * <td>NotAuthorizedException</td>
 * </tr>
 * <tr>
 * <td>403</td>
 * <td>ForbiddenException</td>
 * </tr>
 * <tr>
 * <td>404</td>
 * <td>NotFoundException</td>
 * </tr>
 * <tr>
 * <td>405</td>
 * <td>NotAllowedException</td>
 * </tr>
 * <tr>
 * <td>406</td>
 * <td>NotAcceptableException</td>
 * </tr>
 * <tr>
 * <td>409</td>
 * <td>ConflictException</td>
 * </tr>
 * <tr>
 * <td>415</td>
 * <td>NotSupportedException</td>
 * </tr>
 * <tr>
 * <td>500</td>
 * <td>InternalServerErrorException</td>
 * </tr>
 * <tr>
 * <td>503</td>
 * <td>ServiceUnavailableException</td>
 * </tr>
 * </tbody>
 * </table>
 */
class ExceptionMapper {

    private final Map<Class<? extends Exception>, Function<? extends Exception, Response>> responseMappers = new HashMap<>();

    {
        addMapper(JSONException.class, this::response);
        addMapper(IllegalArgumentException.class, this::response);
        addMapper(IllegalStateException.class, this::response);
    }

    private <E extends Exception> void addMapper(Class<E> type, Function<E, Response> mapper) {
        responseMappers.put(type, mapper);
    }

    @SuppressWarnings("unchecked")
    private <E extends Exception> Function<? super E, Response> getMapper(Class<E> type) {
        if (responseMappers.containsKey(type)) {
            return (Function<E, Response>) responseMappers.get(type);
        } else {
            return this::responseInternal;
        }
    }

    public WebApplicationException mapToRestException(Exception exc) {
        return new WebApplicationException(map(exc));
    }

    private <E extends Exception> Response map(E exc) {
        @SuppressWarnings("unchecked")
        Class<E> type = (Class<E>) exc.getClass();
        return getMapper(type).apply(exc);
    }

    private Response response(JSONException exc) {
        return response(Response.Status.BAD_REQUEST, exc.getMessage());
    }

    private Response response(IllegalStateException exc) {
        return response(Response.Status.BAD_REQUEST, exc.getMessage());
    }

    private Response response(IllegalArgumentException exc) {
        return response(Response.Status.BAD_REQUEST, exc.getMessage());
    }

    private Response responseInternal(Exception exc) {
        return response(Response.Status.INTERNAL_SERVER_ERROR, "Internal server error");
    }

    private Response response(Response.Status status, String message) {
        return Response.status(status).entity(message).type(MediaType.TEXT_PLAIN).build();
    }
}
