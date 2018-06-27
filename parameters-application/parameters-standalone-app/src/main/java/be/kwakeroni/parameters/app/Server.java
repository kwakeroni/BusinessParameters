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
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

class Server implements AutoCloseable {

    public static final int DEFAULT_PORT = 8080;
    public static final String DEFAULT_CONTEXT_PATH = "parameters";

    private final Configuration configuration;
    private java.nio.file.Path webappDir;
    private HttpServer httpServer;


    Server(Configuration configuration) {
        this.configuration = configuration;
    }

    synchronized void start() throws IOException {
        if (this.httpServer != null) return;

        String workDir = configuration.getWorkDirectory()
                .filter(s -> !s.isEmpty())
                .orElse("./work");

        this.webappDir = Paths.get(workDir).resolve("webapp");

        prepareWebApp();

        String uri = String.format("http://127.0.0.1:%s/%s",
                this.configuration.getPort().orElse(DEFAULT_PORT),
                this.configuration.getContextPath().orElse(DEFAULT_CONTEXT_PATH));

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

    private void prepareWebApp() throws IOException {
        Files.createDirectories(this.webappDir);
        unzip(() -> Server.class.getResourceAsStream("/parameters-management-web.jar"), this.webappDir);
    }

    private static void unzip(Supplier<InputStream> stream, java.nio.file.Path location) throws IOException {
        try (ZipInputStream zipStream = new ZipInputStream(stream.get())) {
            for (ZipEntry entry = zipStream.getNextEntry(); entry != null; entry = zipStream.getNextEntry()) {
                java.nio.file.Path dest = location.resolve(entry.getName());
                if (entry.isDirectory()) {
                    Files.createDirectory(dest);
                } else {
                    Files.copy(zipStream, dest);
                }
            }
            zipStream.closeEntry();
        }
    }

    private ResourceConfig getResourceConfig() {

        Root root = new Root();
        RestBackendAdapter restBackend = new RestBackendAdapterFactory().newInstance();
        RestParameterManagement restManagement = new RestParameterManagementFactory().newInstance();

        @Path("/web")
        class WebManagement extends StaticContent {
            private WebManagement() {
                super(webappDir, "index.html");
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
