package be.kwakeroni.parameters.app;

import be.kwakeroni.parameters.backend.api.Configuration;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.extension.mockito.MockitoExtension;
import org.junit.rules.TemporaryFolder;
import org.mockito.Mock;
import org.mockito.Mockito;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.Optional;

import static be.kwakeroni.test.assertion.RestAssert.assertThat;
import static be.kwakeroni.test.assertion.RestAssert.get;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@SuppressWarnings("WeakerAccess")
@ExtendWith(MockitoExtension.class)
class ServerTest {

    private static final int PORT = 9999;
    private static final String CONTEXT_PATH = "parameters";
    private static final String CONTEXT_URL = "http://127.0.0.1:9999/parameters";
    private static final String REST_URL = "http://127.0.0.1:9999/parameters/client/";
    private static final String MGMT_REST_URL = "http://127.0.0.1:9999/parameters/management/";
    private static final String MGMT_WEB_URL = "http://127.0.0.1:9999/parameters/web/";
    private static final String MGMT_WEB_INDEX = "http://127.0.0.1:9999/parameters/web/index.html";


    private TemporaryFolder temporaryFolder = new TemporaryFolder();
    private File workFolder;

    @Mock
    private Configuration configuration;

    @BeforeEach
    void setupConfiguration() throws IOException {
        temporaryFolder.create();
        this.workFolder = new File(temporaryFolder.getRoot(), "work");
        when(configuration.get(Config.WORK_DIRECTORY)).thenReturn(Optional.of(workFolder.getAbsoluteFile().toPath()));
        when(configuration.get(Config.PORT)).thenReturn(Optional.of(PORT));
        when(configuration.get(Config.CONTEXT_PATH)).thenReturn(Optional.of(CONTEXT_PATH));
        ServerConfigurationProvider.setConfiguration(configuration);
    }

    @AfterEach
    void cleanup() {
        ServerConfigurationProvider.clear();
        temporaryFolder.delete();
    }

    public static void main(String[] args) throws Exception {
        ServerTest test = new ServerTest();
        try {
            test.configuration = Mockito.mock(Configuration.class);
            test.setupConfiguration();

            try (Server server = new Server(test.configuration)) {
                server.start();
                JOptionPane.showMessageDialog(null, "Test server running...");
            }

        } finally {
            test.cleanup();
        }
    }

    @Test
    @DisplayName("Loads configuration from the ServerConfigurationProvider")
    void testLoadConfiguration() {
        assertThat(Server.loadConfiguration()).isSameAs(configuration);
    }

    @Nested
    @DisplayName("Unpacks the web application")
    class UnpackWebAppTest {

        @Test
        @DisplayName("To the configured workdirectory")
        void testCreateConfiguredWorkDir() throws Exception {
            assertThat(workFolder).doesNotExist();
            try (Server server = new Server()) {
                server.start();

                assertThat(workFolder).exists();
                assertThat(new File(new File(workFolder, "webapp"), "index.html"))
                        .exists()
                        .isFile()
                        .canRead()
                        .hasSameContentAs(new File(ServerTest.class.getResource("/webapp/index.html").toURI()));
            }
        }

        @Test
        @DisplayName("Overwriting the workdirectory if it already exists")
        void testOverwriteExistingWorkDir() throws Exception {
            assertThat(workFolder).doesNotExist();
            File index = new File(new File(workFolder, "webapp"), "index.html");
            long lastModified = -1;

            try (Server server = new Server()) {
                server.start();

                assertThat(workFolder).exists();
                assertThat(index)
                        .exists()
                        .isFile()
                        .canRead()
                        .hasSameContentAs(new File(ServerTest.class.getResource("/webapp/index.html").toURI()));
                lastModified = index.lastModified();
            }
            Thread.sleep(1000);
            assertThat(workFolder).exists();
            try (Server server = new Server()) {
                server.start();

                assertThat(workFolder).exists();
                assertThat(index)
                        .exists()
                        .isFile()
                        .canRead()
                        .hasSameContentAs(new File(ServerTest.class.getResource("/webapp/index.html").toURI()));

                assertThat(lastModified).isGreaterThan(0);
                assertThat(new Date(index.lastModified())).isAfter(new Date(lastModified));
            }

        }

        @Test
        @DisplayName("Below the runtime folder when there is no such configuration")
        @Disabled("Taints the runtime folder")
        void testCreateDefaultWorkDir() throws Exception {
            when(configuration.get(Config.WORK_DIRECTORY)).thenReturn(Optional.empty());

            File defaultFolder = new File("./work");

            assertThat(defaultFolder).doesNotExist();
            try (Server server = new Server()) {
                server.start();

                assertThat(defaultFolder).exists();
                assertThat(new File(new File(defaultFolder, "webapp"), "index.html"))
                        .exists()
                        .isFile()
                        .canRead()
                        .hasSameContentAs(new File(ServerTest.class.getResource("/webapp/index.html").toURI()));
            }
        }

    }

    @Nested
    @DisplayName("Starts a server")
    class StartServerTest {

        @Test
        @DisplayName("At the configured URL")
        void testStart() throws Exception {
            try (Server server = new Server()) {
                server.start();

                assertThat(get(CONTEXT_URL))
                        .isSuccess()
                        .andText().isEqualTo("Business Parameters");
            }
        }

        @Test
        @DisplayName("Exposing the Parameters REST Service")
        void testRestService() throws Exception {
            try (Server server = new Server()) {
                server.start();

                assertThat(get(REST_URL))
                        .isSuccess()
                        .andText().isEqualTo("Business Parameters Rest Adapter");

            }
        }

        @Test
        @DisplayName("Exposing the Management REST Service")
        void testManagementRestService() throws Exception {
            try (Server server = new Server()) {
                server.start();

                assertThat(get(MGMT_REST_URL))
                        .isSuccess()
                        .andText().isEqualTo("Business Parameters Management Rest Service");

            }
        }

        @Test
        @DisplayName("Exposing the Management Web Application")
        void testManagementWebApp() throws Exception {
            try (Server server = new Server()) {
                server.start();

                assertThat(get(MGMT_WEB_INDEX))
                        .isSuccess()
                        .andResponse()
                        .hasSameContentAs(ServerTest.class.getResourceAsStream("/webapp/index.html"));
            }
        }

        @Test
        @DisplayName("Redirecting to the Management Web application index.html")
        void testManagementWebAppRedirect() throws Exception {
            try (Server server = new Server()) {
                server.start();

                assertThat(get(MGMT_WEB_URL))
                        .redirectsTo(MGMT_WEB_INDEX);
            }
        }

    }

    @Nested
    @DisplayName("Stops a server")
    class StopServerTest {

        @Test
        @DisplayName("Using the stop command")
        void testStop() throws Exception {
            try (Server server = new Server()) {
                server.start();
                assertThat(get(CONTEXT_URL)).isSuccess();

                server.stop();
                assertThat(get(CONTEXT_URL)).failsToConnect();
            }
        }

        @Test
        @DisplayName("When closing the Server resource")
        void testClose() throws Exception {
            try (Server server = new Server()) {
                server.start();
                assertThat(get(CONTEXT_URL)).isSuccess();
            }
            assertThat(get(CONTEXT_URL)).failsToConnect();
        }
    }

}
