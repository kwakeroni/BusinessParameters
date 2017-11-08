package be.kwakeroni.parameters.petshop;

import be.kwakeroni.parameters.adapter.rest.RestBackendAdapter;
import be.kwakeroni.parameters.adapter.rest.factory.RestBackendAdapterFactory;
import be.kwakeroni.parameters.client.api.BusinessParameters;
import be.kwakeroni.parameters.client.api.factory.BusinessParametersFactory;
import be.kwakeroni.parameters.management.rest.RestParameterManagement;
import be.kwakeroni.parameters.management.rest.factory.RestParameterManagementFactory;
import be.kwakeroni.parameters.petshop.rest.PetshopRestService;
import be.kwakeroni.parameters.petshop.service.ParametersPriceCalculator;
import com.sun.jersey.api.container.ContainerFactory;
import com.sun.jersey.api.container.httpserver.HttpServerFactory;
import com.sun.jersey.api.core.ApplicationAdapter;
import com.sun.jersey.api.core.ResourceConfig;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Response;
import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * Created by kwakeroni on 07/11/17.
 */
public class PetshopApplication {

    public static void main(String[] args) throws Exception {
        try (
                Server parameters = new Server(8080, "/parameters",
                        createAdapter(),
                        createManagement(),
                        new ParametersWebAppService()
                );
                Server petshop = new Server(8081, "/",
                        createPetshopRestService(),
                        new PetshopWebAppService()
                );
        ) {
            parameters.start();
            petshop.start();
            System.out.println("Enter 'q' to stop server.");
            boolean running = true;
            while (running) {
                if ('q' == System.in.read()) {
                    running = false;
                }
            }
        }

    }


    public PetshopApplication() {

    }

    private static class Server implements AutoCloseable {

        private HttpServer server;

        public Server(int port, String root, Object... resources) throws IOException {
            ResourceConfig config = new ApplicationAdapter(ofSingletons(resources));
            config.setPropertiesAndFeatures(Collections.singletonMap("com.sun.jersey.api.json.POJOMappingFeature", true));
//        config.getContainerResponseFilters().add(new CORSFilter());

            this.server = HttpServerFactory.create("http://127.0.0.1:" + port + root,
                    ContainerFactory.createContainer(HttpHandler.class, config, null));
        }

        public void start() {
            this.server.start();
        }

        @Override
        public void close() throws Exception {
            if (this.server != null) this.server.stop(0);

        }
    }

    private static PetshopRestService createPetshopRestService() {
        BusinessParameters parameters = ServiceLoader.load(BusinessParametersFactory.class).iterator().next().getInstance();
        return new PetshopRestService(new ParametersPriceCalculator(parameters));
    }

    private static RestBackendAdapter createAdapter() {
        RestBackendAdapterFactory factory = new RestBackendAdapterFactory();
        // factory.setBackendType(testData::acceptBackend);
        return factory.newInstance();
    }

    private static RestParameterManagement createManagement() {
        RestParameterManagementFactory factory = new RestParameterManagementFactory();
        //factory.setBackendType(testData::acceptBackend);
        return factory.newInstance();
    }

    private static Application ofSingletons(Object... singletons) {
        return new Application() {
            @Override
            public Set<Object> getSingletons() {
                return new HashSet<>(Arrays.asList(singletons));
            }
        };
    }


    public static abstract class AbstractWebAppService {

        private final String resourcePath;

        public AbstractWebAppService(String resourcePath) {
            this.resourcePath = resourcePath;
        }

        @GET
        @Path("/{path:.*}")
        public Response get(@PathParam("path") String path) throws Exception {
            String webPath = resourcePath + "/" + path;
            File file = new File(PetshopApplication.class.getResource(webPath).toURI());

            return Response.ok(file)
                    // .header("Content-Disposition", "attachment; filename=\"" + file.getName() + "\"" ) //optional
                    .build();
        }

//        private MediaType getMediaType(File file) {
//            String name = file.getName().toLowerCase();
//            if (name.endsWith(".html") || name.endsWith(".htm")) {
//                return MediaType.TEXT_HTML_TYPE;
//            } else {
//                return MediaType.APPLICATION_OCTET_STREAM_TYPE;
//            }
//        }

    }

    @Path("/petshop")
    public static class PetshopWebAppService extends AbstractWebAppService {
        public PetshopWebAppService() {
            super("/petshop-webapp");
        }
    }

    @Path("/web")
    public static class ParametersWebAppService extends AbstractWebAppService {
        public ParametersWebAppService() {
            super("/parameters-webapp");
        }
    }
}
