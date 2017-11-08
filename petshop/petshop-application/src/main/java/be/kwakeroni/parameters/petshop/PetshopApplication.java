package be.kwakeroni.parameters.petshop;

import be.kwakeroni.parameters.petshop.rest.PetshopRestService;
import be.kwakeroni.parameters.petshop.rest.WebAppService;
import be.kwakeroni.parameters.petshop.service.PriceCalculator;
import com.sun.jersey.api.container.ContainerFactory;
import com.sun.jersey.api.container.httpserver.HttpServerFactory;
import com.sun.jersey.api.core.ApplicationAdapter;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import javax.ws.rs.core.Application;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by kwakeroni on 07/11/17.
 */
public class PetshopApplication {

    private int port = 8080;
    private HttpServer server;

    public static void main(String[] args) throws Exception {
        PetshopApplication application = new PetshopApplication();
        application.setUp();
        try (AutoCloseable closeable = application::tearDown) {
            application.start();
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

    private void setUp() throws IOException {
        Application application = createApplication();
        ApplicationAdapter config = new ApplicationAdapter(application);
        config.setPropertiesAndFeatures(Collections.singletonMap("com.sun.jersey.api.json.POJOMappingFeature", true));
//        config.getContainerResponseFilters().add(new CORSFilter());

        this.server = HttpServerFactory.create("http://127.0.0.1:" + port + "/",
                ContainerFactory.createContainer(HttpHandler.class, config, null));

    }

    private void start() {
        this.server.start();
    }


    private void tearDown() {
        System.out.println("Shutting down...");
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
                return new HashSet<>(Arrays.asList(
                        new PetshopRestService(new PriceCalculator()),
                        new WebAppService()
                ));
            }
        };
    }

}
