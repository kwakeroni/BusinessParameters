package be.kwakeroni.scratch.env.rest;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.container.ContainerFactory;
import com.sun.jersey.api.container.httpserver.HttpServerFactory;
import com.sun.jersey.api.core.ApplicationAdapter;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import javax.ws.rs.core.Application;
import java.io.IOException;
import java.util.Collections;
import java.util.Set;
import java.util.function.Supplier;

/**
 * (C) 2016 Maarten Van Puymbroeck
 */
public class RestServer {

    String baseUrl;
    Supplier<? extends Set<Object>> resources;
    HttpServer server;

    public RestServer(String baseUrl, Supplier<? extends Set<Object>> resources) {
        this.baseUrl = baseUrl;
        this.resources = resources;
    }

    public RestServer(String baseUrl, Set<Object> resources) {
        this(baseUrl, () -> resources);
    }

    private String resolve(String base, String path) {
        return base + ((path.startsWith("/")) ? "" : "/") + path;
    }

    public WebResource call(String path) {
        return new Client().resource(resolve(baseUrl, path));
    }

    protected void setUp() throws IOException {
        Application application = createApplication();
        ApplicationAdapter config = new ApplicationAdapter(application);
        config.setPropertiesAndFeatures(Collections.singletonMap("com.sun.jersey.api.json.POJOMappingFeature", true));

        this.server = HttpServerFactory.create(this.baseUrl,
                ContainerFactory.createContainer(HttpHandler.class, config, null));

        this.server.start();
    }

    protected void tearDown() {
        if (this.server != null) this.server.stop(0);
    }

    private Application createApplication() {
        return new Application() {
            @Override
            public Set<Class<?>> getClasses() {
                return Collections.emptySet();
//                return new HashSet<>(Arrays.asList(SMSProxyReceiveWS.class, SMSProxySendWS.class));
            }

            @Override
            public Set<Object> getSingletons() {
                return resources.get();
            }
        };
    }
}
