package be.kwakeroni.parameters.app.support;

import be.kwakeroni.test.extension.TemporaryFolderExtension;
import com.sun.jersey.api.container.ContainerFactory;
import com.sun.jersey.api.container.httpserver.HttpServerFactory;
import com.sun.jersey.api.core.ApplicationAdapter;
import com.sun.jersey.api.core.ResourceConfig;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.rules.TemporaryFolder;

import javax.ws.rs.Path;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Response;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.Arrays;
import java.util.Collections;
import java.util.Set;
import java.util.function.Function;

import static be.kwakeroni.test.assertion.RestAssert.assertThat;
import static be.kwakeroni.test.assertion.RestAssert.get;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(TemporaryFolderExtension.class)
public class StaticContentResourceTest {

    private static final String CONTEXT_PATH = "/content";
    private static final String INDEX_PAGE = "index.test";
    private static final String SERVER_URL = "http://127.0.0.1:9999/test";
    private static final String CONTEXT_URL = SERVER_URL + CONTEXT_PATH;
    private static final String INDEX_URL = CONTEXT_URL + "/" + INDEX_PAGE;
    private static final String IMAGES = "images";
    private static final String IMAGES_URL = CONTEXT_URL + "/" + IMAGES;
    private static final String IMAGE = "images/test.jpg";
    private static final String IMAGE_URL = CONTEXT_URL + "/" + IMAGE;
    private static final String NON_READABLE = "private.txt";
    private static final String NON_READABLE_URL = CONTEXT_URL + "/" + NON_READABLE;

    private static HttpServer httpServer;
    private static TemporaryFolder contentDirectory = new TemporaryFolder();

    @SuppressWarnings("unchecked")
    private static Function<java.nio.file.Path, Response.ResponseBuilder> getFileContents = mock(Function.class);

    @Path(CONTEXT_PATH)
    public static class TestStaticContentResource extends StaticContentResource {
        private TestStaticContentResource(java.nio.file.Path contentDirectory) {
            super(contentDirectory, INDEX_PAGE);
        }

        @Override
        protected Response.ResponseBuilder getContents0(java.nio.file.Path resource) throws Exception {
            Response.ResponseBuilder mockResult = getFileContents.apply(resource);
            return (mockResult == null) ? super.getContents0(resource) : mockResult;
        }
    }

    @BeforeAll
    static void setUpRestService() throws Exception {

        contentDirectory.create();

        // File outside the content directory to make sure this file cannot be accessed
        java.nio.file.Path outsideFilePath = contentDirectory.getRoot().toPath().getParent().resolve(INDEX_PAGE);
        Files.write(outsideFilePath, Arrays.asList("one", "two", "three"));

        // Non-readable file to make sure this file cannot be accessed
        java.nio.file.Path nonReadablePath = contentDirectory.getRoot().toPath().resolve(NON_READABLE);
        Files.write(nonReadablePath, Arrays.asList("one", "two", "three"));
        Files.setPosixFilePermissions(nonReadablePath, PosixFilePermissions.fromString("--x------"));

        java.nio.file.Path imagePath = contentDirectory.getRoot().toPath().resolve(IMAGE);
        Files.createDirectories(imagePath.getParent());
        Files.copy(getImage(), imagePath);

        ResourceConfig config = new ApplicationAdapter(new Application() {
            @Override
            public Set<Object> getSingletons() {
                return Collections.singleton(new TestStaticContentResource(contentDirectory.getRoot().toPath()));
            }
        });
        config.setPropertiesAndFeatures(Collections.singletonMap("com.sun.jersey.api.json.POJOMappingFeature", true));

        httpServer = HttpServerFactory.create(SERVER_URL,
                ContainerFactory.createContainer(HttpHandler.class, config, null));

        httpServer.start();
    }

    @AfterEach
    @SuppressWarnings("unchecked")
    void tearDown() {
        reset(getFileContents);
    }

    @AfterAll
    static void tearDownRestService() {
        httpServer.stop(0);
        httpServer = null;
        contentDirectory.delete();
    }

    @Test
    @DisplayName("Redirects root context to the index page (without trailing slash)")
    void testRedirectRootWithoutSlash() {
        assertThat(get(CONTEXT_URL))
                .redirectsTo(INDEX_URL);
    }

    @Test
    @DisplayName("Redirects root context to the index page (with trailing slash)")
    void testRedirectRootWithSlash() {
        assertThat(get(CONTEXT_URL + "/"))
                .redirectsTo(INDEX_URL);
    }

    @Test
    @DisplayName("Serves files from a local folder")
    void testServesFiles() {
        assertThat(get(IMAGE_URL))
                .isSuccess()
                .hasContentType("image/jpeg")
                .andResponse().hasSameContentAs(getImage());
    }

    @Test
    @DisplayName("Does not serve files outside the local folder")
    void testServesNoFilesOutsideFolder() {
        assertThat(get(CONTEXT_URL + "/../" + INDEX_PAGE))
                .isNotFound();
    }

    @Test
    @DisplayName("Reports non-existing files as not found")
    void testNotFoundNonExistingFiles() {
        assertThat(get(INDEX_URL))
                .isNotFound();
    }

    @Test
    @DisplayName("Reports directories as not found")
    void testNotFoundDirectories() {
        assertThat(get(IMAGES_URL))
                .isNotFound();
    }

    @Test
    @DisplayName("Reports non-readable files as not found")
    void testNotFoundNonReadableFiles() {
        assertThat(get(NON_READABLE_URL))
                .isNotFound();
    }

    @Test
    @DisplayName("Reports blank server error in case of exceptions")
    void testServerError() {
        when(getFileContents.apply(any())).thenThrow(new UnsupportedOperationException());

        assertThat(get(IMAGE_URL))
                .hasStatus(Response.Status.INTERNAL_SERVER_ERROR)
                .andText().isEmpty();
    }

    private static InputStream getImage() {
        return StaticContentResourceTest.class.getResourceAsStream("test.jpg");
    }
}
