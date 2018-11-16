package be.kwakeroni.parameters.app.support;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.nio.file.Files;
import java.util.Optional;

public abstract class StaticContentResource {

    private static final Logger LOG = LoggerFactory.getLogger(StaticContentResource.class);

    private final java.nio.file.Path contentDirectory;
    private final String indexPage;

    protected StaticContentResource(StaticContent content) {
        this(content.getContentDirectory(), content.getIndexPage());
    }

    protected StaticContentResource(java.nio.file.Path contentDirectory, String indexPage) {
        this.contentDirectory = contentDirectory.toAbsolutePath().normalize();
        this.indexPage = indexPage;
    }


    @GET
    public Response getInfo(@Context UriInfo uriInfo) {
        URI requestUri = uriInfo.getRequestUri();
        URI contextUri = (requestUri.getPath().endsWith("/")) ? requestUri : requestUri.resolve(requestUri.getPath() + "/");

        return Response.seeOther(contextUri.resolve(indexPage)).build();
    }

    @GET
    @Path("{path:.*}")
    public Response get(@PathParam("path") String path) {

        java.nio.file.Path resourcePath = contentDirectory.resolve(path);

        return ifExists(resourcePath)
                .map(this::getContents)
                .orElseGet(() -> Response.status(Response.Status.NOT_FOUND))
                .build();
    }

    private Optional<java.nio.file.Path> ifExists(java.nio.file.Path resource) {
        // Do not serve files outside of the content directory.
        // For security reasons it is preferable to treat such requests like non-existing files (404)
        // rather than returning a 'forbidden' response (403).

        return Optional.of(resource)
                .map(java.nio.file.Path::normalize)
                .filter(path -> path.startsWith(contentDirectory))
                .filter(Files::exists)
                .filter(Files::isRegularFile)
                .filter(Files::isReadable);
    }

    private Response.ResponseBuilder getContents(java.nio.file.Path resource) {
        try {
            return getContents0(resource);
        } catch (Exception exc) {
            LOG.error("Unable to fetch content of file {}", exc);
            return Response.serverError();
        }
    }

    Response.ResponseBuilder getContents0(java.nio.file.Path resource) throws Exception {
        return Response.ok()
                .type(Files.probeContentType(resource))
                .entity(Files.newInputStream(resource));
    }
}
