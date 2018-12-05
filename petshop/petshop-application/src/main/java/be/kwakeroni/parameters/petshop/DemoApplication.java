package be.kwakeroni.parameters.petshop;

import be.kwakeroni.parameters.app.Application;
import be.kwakeroni.parameters.app.support.MainWaiter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

public class DemoApplication {

    private static final Logger LOG = LoggerFactory.getLogger(PetshopApplication.class);

    public static void main(String[] args) throws Exception {

        initLogging();

        Properties properties = new Properties();
        properties.load(PetshopApplication.class.getResourceAsStream("/petshop.properties"));
        initDemoEnvironment(properties);



        final PetshopApplication petshop = PetshopApplication.create(properties)
                .orElseThrow(() -> new IllegalStateException("Unable to start PetshopApplication"));

        final Application app = Application.create(properties)
                .orElseThrow(() -> new IllegalStateException("Unable to start Business Parameters Application"));

        try (AutoCloseable closeableApp = app.start();
             AutoCloseable closeablePetshop = petshop.start()) {
            MainWaiter.waitForExit();
        }

    }

    private static void initLogging() {
        System.out.println("log4j.configuration=" + System.getProperty("log4j.configuration"));
        if (System.getProperty("log4j.configuration") == null) {
            if (Thread.currentThread().getContextClassLoader().getResource("log4j.properties") == null) {
                System.setProperty("log4j.configuration", "log4j.fallback.properties");
            }
        }
    }


    private static void initDemoEnvironment(Properties properties) throws IOException {
        Path storageFolder = Paths.get(properties.getProperty("inmemory.storage.folder"));
        if (!Files.exists(storageFolder)){
            LOG.info("Creating demo storage folder at: " + storageFolder.toAbsolutePath());
            Files.createDirectories(storageFolder);
        }
    }

}
