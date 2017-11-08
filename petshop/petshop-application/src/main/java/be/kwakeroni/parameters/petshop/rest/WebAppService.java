package be.kwakeroni.parameters.petshop.rest;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.File;

/**
 * Created by kwakeroni on 07/11/17.
 */
@Path("/webapp")
public class WebAppService {

    @GET
    @Path("/{path:.*}")
//    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response get(@PathParam("path") String path) throws Exception {
        String webPath = "/petshop-webapp/" + path;
        File file = new File(WebAppService.class.getResource(webPath).toURI());

        return Response.ok(file, getMediaType(file))
                // .header("Content-Disposition", "attachment; filename=\"" + file.getName() + "\"" ) //optional
                .build();
    }

    private MediaType getMediaType(File file) {
        String name = file.getName().toLowerCase();
        if (name.endsWith(".html") || name.endsWith(".htm")) {
            return MediaType.TEXT_HTML_TYPE;
        } else {
            return MediaType.APPLICATION_OCTET_STREAM_TYPE;
        }
    }

}
