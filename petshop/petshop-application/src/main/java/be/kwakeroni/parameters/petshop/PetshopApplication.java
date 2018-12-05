package be.kwakeroni.parameters.petshop;

import be.kwakeroni.parameters.app.support.MainWaiter;
import be.kwakeroni.parameters.app.support.SimpleRestServer;
import be.kwakeroni.parameters.app.support.StaticContent;
import be.kwakeroni.parameters.app.support.StaticContentFactory;
import be.kwakeroni.parameters.app.support.StaticContentResource;
import be.kwakeroni.parameters.client.api.BusinessParameters;
import be.kwakeroni.parameters.client.api.factory.BusinessParametersFactory;
import be.kwakeroni.parameters.petshop.rest.PetshopRestService;
import be.kwakeroni.parameters.petshop.service.AnimalCatalog;
import be.kwakeroni.parameters.petshop.service.ContactService;
import be.kwakeroni.parameters.petshop.service.PriceCalculator;

import javax.ws.rs.Path;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.ServiceLoader;
import java.util.function.Function;
import java.util.stream.Collectors;

public class PetshopApplication {

    @SuppressWarnings("WeakerAccess")
    public static final int DEFAULT_PORT = 8081;
    @SuppressWarnings("WeakerAccess")
    public static final String DEFAULT_CONTEXT_PATH = "";

    private final Server server;

    public static void main(String[] args) throws Exception {
        initLogging();
        Optional<PetshopApplication> app = create(args);
        if (app.isPresent()) {
            try (AutoCloseable closeable = app.get().start()) {
                MainWaiter.waitForExit();
            }
        }
    }

    public static Optional<PetshopApplication> create(String... args) throws IOException {
        return createServer(args)
                .map(PetshopApplication::new);
    }

    public static Optional<PetshopApplication> create(Properties configurationProperties) throws IOException {
        return createServer(configurationProperties)
                .map(PetshopApplication::new);
    }

    private PetshopApplication(Server server) {
        this.server = server;
    }


    private static void initLogging() {
        System.out.println("log4j.configuration=" + System.getProperty("log4j.configuration"));
        if (System.getProperty("log4j.configuration") == null) {
            if (Thread.currentThread().getContextClassLoader().getResource("log4j.properties") == null) {
                System.setProperty("log4j.configuration", "log4j.fallback.properties");
            }
        }
    }

    public AutoCloseable start() throws IOException {
        this.server.start();
        return this.server;
    }

    static Optional<Server> createServer(String... args) throws IOException {
        Properties properties = new Properties();
        properties.load(PetshopApplication.class.getResourceAsStream("/petshop.properties"));
        return createServer(properties);
    }

    static Optional<Server> createServer(Properties properties) throws IOException {
        Map<String, String> propertyMap = properties.stringPropertyNames().stream().collect(Collectors.toMap(Function.identity(), properties::getProperty));
        return Optional.of(new Server(propertyMap));
    }


    private static class Server extends SimpleRestServer {

        private final Map<String, String> properties;

        public Server(Map<String, String> properties) {
            this.properties = properties;
        }

        @Override
        protected int getPort() {
            return DEFAULT_PORT;
        }

        @Override
        protected String getContextPath() {
            return DEFAULT_CONTEXT_PATH;
        }

        @Override
        protected String getHelloMessage() {
            return "Business Parameters Petshop";
        }

        @Override
        protected Collection<Object> getResources() {
            StaticContentResource webApp = getWebApp();
            PetshopRestService restService = createPetshopRestService(properties);
            return Arrays.asList(restService, webApp);
        }


        private PetshopRestService createPetshopRestService(Map<String, String> properties) {
            BusinessParameters parameters = createBusinessParameters(properties);
            return new PetshopRestService(
                    new AnimalCatalog(parameters),
                    new PriceCalculator(parameters),
                    new ContactService(parameters));
        }

        private BusinessParameters createBusinessParameters(Map<String, String> properties) {
            return ServiceLoader.load(BusinessParametersFactory.class).iterator().next().getInstance(properties);
        }

        private StaticContentResource getWebApp() {
            @Path("/petshop")
            class PetshopWebApp extends StaticContentResource {
                public PetshopWebApp() {
                    super(getWebContent());
                }
            }


            return new PetshopWebApp();
        }

        private StaticContent getWebContent() {
            return StaticContentFactory.fromZip(this::getManagementWebZip, "petshop/index.html", getWebappDir());
        }

        private java.nio.file.Path getWebappDir() {
            return Optional.ofNullable(properties.get("server.workDir"))
                    .map(Paths::get)
                    .orElseGet(() -> Paths.get("./work"))
                    .resolve("petshop");
        }

        private InputStream getManagementWebZip() {
            return PetshopApplication.class.getResourceAsStream("/petshop-web.jar");
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

}
