package be.kwakeroni.parameters.app.support;

import com.sun.jersey.api.container.ContainerFactory;
import com.sun.jersey.api.container.httpserver.HttpServerFactory;
import com.sun.jersey.api.core.ApplicationAdapter;
import com.sun.jersey.api.core.ResourceConfig;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Application;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;


public abstract class SimpleRestServer implements AutoCloseable {

    private static final Logger LOG = LoggerFactory.getLogger(SimpleRestServer.class);

    private HttpServer httpServer;

    protected abstract int getPort();

    protected abstract String getContextPath();

    protected abstract Collection<Object> getResources();

    protected abstract String getHelloMessage();

    protected synchronized void start() throws IOException {
        if (this.httpServer != null) return;


        String uri = String.format("http://127.0.0.1:%s/%s",
                getPort(),
                getContextPath());

        LOG.info("Starting server at {}", uri);

        this.httpServer = HttpServerFactory.create(uri,
                ContainerFactory.createContainer(HttpHandler.class, getResourceConfig(), null));


        this.httpServer.start();

        LOG.info("Server ready at " + uri);
    }

    protected synchronized void stop() {
        if (this.httpServer != null) {
            LOG.info("Shutting down server at {}", this.httpServer.getAddress());
            this.httpServer.stop(0);
            this.httpServer = null;
        }
    }


    @Override
    public void close() {
        this.stop();
    }


    protected ResourceConfig getResourceConfig() {
        Set<Object> resources = new HashSet<>();
        resources.add(getRootResource());
        resources.addAll(getResources());

        Application application = new Application() {
            @Override
            public Set<Object> getSingletons() {
                return resources;
            }
        };

        ResourceConfig config = new ApplicationAdapter(application);
        config.setPropertiesAndFeatures(Collections.singletonMap("com.sun.jersey.api.json.POJOMappingFeature", true));
        return config;

    }

    protected Object getRootResource() {


        return new Root();
    }

    @Path("/")
    public final class Root {
        private Root() {

        }

        @GET
        public String hello() {
            return getHelloMessage();
        }
    }

}
