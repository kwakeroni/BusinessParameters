package be.kwakeroni.parameters.app;

import be.kwakeroni.parameters.adapter.rest.RestBackendAdapter;
import be.kwakeroni.parameters.adapter.rest.factory.RestBackendAdapterFactory;
import be.kwakeroni.parameters.management.rest.RestParameterManagement;
import be.kwakeroni.parameters.management.rest.factory.RestParameterManagementFactory;
import com.sun.jersey.api.container.ContainerFactory;
import com.sun.jersey.api.container.httpserver.HttpServerFactory;
import com.sun.jersey.api.core.ApplicationAdapter;
import com.sun.jersey.api.core.ResourceConfig;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Application;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

class Server implements AutoCloseable {

    private final Configuration configuration;
    private HttpServer httpServer;


    Server(Configuration configuration) {
        this.configuration = configuration;
    }

    synchronized void start() throws IOException {
        if (this.httpServer != null) return;

        String uri = String.format("http://127.0.0.1:%s/%s", this.configuration.getPort(), this.configuration.getContextPath());

        this.httpServer = HttpServerFactory.create(uri,
                ContainerFactory.createContainer(HttpHandler.class, getResourceConfig(), null));

        this.httpServer.start();

    }

    synchronized void stop() {
        if (this.httpServer != null) {
            this.httpServer.stop(0);
            this.httpServer = null;
        }
    }

    @Override
    public void close() {
        this.stop();
    }

    private ResourceConfig getResourceConfig() {

        Root root = new Root();
        RestBackendAdapter restBackend = new RestBackendAdapterFactory().newInstance();
        RestParameterManagement restManagement = new RestParameterManagementFactory().newInstance();

        @Path("/web")
        class WebManagement extends StaticContent {
            private WebManagement() {
                // TODO path
                super(Paths.get("."), "index.html");
            }
        }
        StaticContent webManagement = new WebManagement();

        ResourceConfig config = new ApplicationAdapter(ofSingletons(root, restBackend, restManagement, webManagement));
        config.setPropertiesAndFeatures(Collections.singletonMap("com.sun.jersey.api.json.POJOMappingFeature", true));
        return config;
    }

    @Path("/")
    public static final class Root {
        @GET
        public String hello() {
            return "Business Parameters";
        }
    }

    private static Application ofSingletons(Object... singletons) {
        return new Application() {
            @Override
            public Set<Object> getSingletons() {
                return new HashSet<>(Arrays.asList(singletons));
            }
        };
    }
}
