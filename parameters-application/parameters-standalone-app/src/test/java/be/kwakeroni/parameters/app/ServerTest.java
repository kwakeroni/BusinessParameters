package be.kwakeroni.parameters.app;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.extension.mockito.MockitoExtension;
import org.mockito.Mock;

import static be.kwakeroni.test.assertion.RestAssert.assertThat;
import static be.kwakeroni.test.assertion.RestAssert.get;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ServerTest {

    private static final int PORT = 9999;
    private static final String CONTEXT_PATH = "parameters";
    private static final String CONTEXT_URL = "http://127.0.0.1:9999/parameters";
    private static final String REST_URL = "http://127.0.0.1:9999/parameters/client/";
    private static final String MGMT_REST_URL = "http://127.0.0.1:9999/parameters/management/";
    private static final String MGMT_WEB_URL = "http://127.0.0.1:9999/parameters/web/";
    private static final String MGMT_WEB_INDEX = "http://127.0.0.1:9999/parameters/web/index.html";


    @Mock
    private Configuration configuration;

    @BeforeEach
    private void setupConfiguration() {
        when(configuration.getPort()).thenReturn(PORT);
        when(configuration.getContextPath()).thenReturn(CONTEXT_PATH);
    }

    @Nested
    @DisplayName("Starts a server")
    class StartServerTest {

        @Test
        @DisplayName("At the configured URL")
        void testStart() throws Exception {
            try (Server server = new Server(configuration)) {
                server.start();

                assertThat(get(CONTEXT_URL))
                        .isSuccess()
                        .andText().isEqualTo("Business Parameters");

            }
        }

        @Test
        @DisplayName("Exposing the Parameters REST Service")
        void testRestService() throws Exception {
            try (Server server = new Server(configuration)) {
                server.start();

                assertThat(get(REST_URL))
                        .isSuccess()
                        .andText().isEqualTo("Business Parameters Rest Adapter");

            }
        }

        @Test
        @DisplayName("Exposing the Management REST Service")
        void testManagementRestService() throws Exception {
            try (Server server = new Server(configuration)) {
                server.start();

                assertThat(get(MGMT_REST_URL))
                        .isSuccess()
                        .andText().isEqualTo("Business Parameters Management Rest Service");

            }
        }

        @Test
        @DisplayName("Exposing the Management Web Application")
        void testManagementWebApp() throws Exception {
            try (Server server = new Server(configuration)) {
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
            try (Server server = new Server(configuration)) {
                server.start();
                assertThat(get(CONTEXT_URL)).isSuccess();

                server.stop();
                assertThat(get(CONTEXT_URL)).failsToConnect();
            }
        }

        @Test
        @DisplayName("When closing the Server resource")
        void testClose() throws Exception {
            try (Server server = new Server(configuration)) {
                server.start();
                assertThat(get(CONTEXT_URL)).isSuccess();
            }
            assertThat(get(CONTEXT_URL)).failsToConnect();
        }
    }

}
