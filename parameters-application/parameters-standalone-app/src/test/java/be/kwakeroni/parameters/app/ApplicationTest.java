package be.kwakeroni.parameters.app;

import be.kwakeroni.parameters.backend.api.Configuration;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.extension.mockito.MockitoExtension;
import org.junit.rules.TemporaryFolder;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class ApplicationTest {

    private TemporaryFolder temporaryFolder = new TemporaryFolder();

    @BeforeEach
    void setUp() throws Exception {
        temporaryFolder.create();
    }

    @AfterEach
    void tearDown() {
        temporaryFolder.delete();
        ServerConfigurationProvider.clear();
    }

    @Nested
    @DisplayName("Creates a server")
    class CreatesServer {

        @Test
        @DisplayName("With default configuration")
        void withDefaultConfiguration() throws Exception {
            Configuration expected = new ServerConfigurationProvider().getConfiguration();
            Server server = createServer("");
            Configuration actual = server.getConfiguration();
            assertThat(actual).isSameAs(expected);
        }

    }

    @Nested
    @DisplayName("Has command line parameters")
    class HasCommandLineParameters {
        @Test
        @DisplayName("-c to select configuration file")
        void selectConfigurationFile() throws Exception {
            Path configFile = temporaryFolder.getRoot().toPath().resolve("config.cfg");
            Files.write(configFile, Collections.singleton("test.property=ABC789"));
            Server server = createServer("-c " + configFile.toAbsolutePath());
            Configuration actual = server.getConfiguration();
            assertThat(actual.getStringParameter("test.property")).hasValue("ABC789");
        }

        @Test
        @DisplayName("--help shows usage information")
        void showUsage() throws Exception {
            createNoServer("--help");
        }
    }

    private static Server createServer(String line) throws Exception {
        Optional<Server> server = createOptServer(line);
        assertThat(server).isPresent();
        return server.orElse(null);
    }

    private static void createNoServer(String line) throws Exception {
        assertThat(createOptServer(line)).isNotPresent();
    }

    private static Optional<Server> createOptServer(String line) throws Exception {
        return Application.createServer(line.split("\\s+"));
    }

}