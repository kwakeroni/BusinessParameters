package be.kwakeroni.parameters.app;

import be.kwakeroni.parameters.adapter.rest.RestBackendAdapter;
import be.kwakeroni.parameters.adapter.rest.factory.RestBackendAdapterFactory;
import be.kwakeroni.parameters.app.support.SimpleRestServer;
import be.kwakeroni.parameters.app.support.StaticContent;
import be.kwakeroni.parameters.app.support.StaticContentFactory;
import be.kwakeroni.parameters.app.support.StaticContentResource;
import be.kwakeroni.parameters.backend.api.Configuration;
import be.kwakeroni.parameters.backend.api.ConfigurationProvider;
import be.kwakeroni.parameters.management.rest.RestParameterManagement;
import be.kwakeroni.parameters.management.rest.factory.RestParameterManagementFactory;

import javax.ws.rs.Path;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.ServiceLoader;

import static be.kwakeroni.parameters.app.Config.*;

class Server extends SimpleRestServer {

    @SuppressWarnings("WeakerAccess")
    public static final int DEFAULT_PORT = 8080;
    @SuppressWarnings("WeakerAccess")
    public static final String DEFAULT_CONTEXT_PATH = "parameters";

    private final Configuration configuration;

    Server() {
        this(loadConfiguration());
    }

    Server(Configuration configuration) {
        this.configuration = configuration;
    }

    static Configuration loadConfiguration() {
        Iterator<ConfigurationProvider> providers = ServiceLoader.load(ConfigurationProvider.class).iterator();
        if (!providers.hasNext()) {
            throw new IllegalStateException("No configuration provider found");
        }
        return providers.next().getConfiguration();
    }

    Configuration getConfiguration() {
        return configuration;
    }

    @Override
    protected int getPort() {
        return this.configuration.get(PORT).orElse(DEFAULT_PORT);
    }

    @Override
    protected String getContextPath() {
        return this.configuration.get(CONTEXT_PATH).orElse(DEFAULT_CONTEXT_PATH);
    }

    @Override
    protected String getHelloMessage() {
        return "Business Parameters";
    }

    @Override
    protected Collection<Object> getResources() {
        StaticContentResource webContent = getWebManagement();
        RestBackendAdapter restBackend = new RestBackendAdapterFactory().newInstance();
        RestParameterManagement restManagement = new RestParameterManagementFactory().newInstance();

        return Arrays.asList(restBackend, restManagement, webContent);
    }

    private StaticContentResource getWebManagement() {

        @Path("/web")
        class WebManagement extends StaticContentResource {
            private WebManagement() {
                super(getManagementWebContent());
            }
        }

        return new WebManagement();

    }

    private StaticContent getManagementWebContent() {
        return StaticContentFactory.fromZip(this::getManagementWebZip, "index.html", getWebappDir());
    }


    private java.nio.file.Path getWebappDir() {
        return configuration.get(WORK_DIRECTORY)
                .orElseGet(() -> Paths.get("./work"))
                .resolve("webapp");
    }

    private InputStream getManagementWebZip() {
        return Server.class.getResourceAsStream("/parameters-management-web.jar");
    }

    @Override
    protected synchronized void start() throws IOException {
        super.start();
    }

    @Override
    protected synchronized void stop() {
        super.stop();
    }
}
