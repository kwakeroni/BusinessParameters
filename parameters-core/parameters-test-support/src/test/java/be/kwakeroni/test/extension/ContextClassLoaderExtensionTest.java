package be.kwakeroni.test.extension;

import be.kwakeroni.test.extension.ContextClassLoaderExtension.AddToClasspath;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(ContextClassLoaderExtensionTest.TestExtension.class)
@ExtendWith(ContextClassLoaderExtension.class)
class ContextClassLoaderExtensionTest {

    private static final String BASE_URL;
    private static final String BASE_PATH;

    static {
        try {
            URL url = ContextClassLoaderExtensionTest.class.getResource("/");
            BASE_URL = removeTrailingSlash(url.toExternalForm());
            BASE_PATH = toPath(url).toString();
        } catch (URISyntaxException exc) {
            throw new ExceptionInInitializerError(exc);
        }
    }

    private static String removeTrailingSlash(String string) {
        return string.substring(0, string.length() - 1);
    }

    private static Path toPath(URL url) throws URISyntaxException {
        return Paths.get(url.toURI()).toAbsolutePath();
    }

    private ClassLoader originalContextClassLoader;

    @BeforeEach
    public void setUp() {
        ContextClassLoaderExtensionTest.this.originalContextClassLoader = Thread.currentThread().getContextClassLoader();
    }

    @AfterEach
    public void endAssert() {
        assertThat(Thread.currentThread().getContextClassLoader()).isSameAs(originalContextClassLoader);
    }

    @Nested
    class FieldBasedTest {

        @AddToClasspath
        private URL url = ContextClassLoaderExtensionTest.class.getResource("/be");
        @AddToClasspath
        private File file = new File(BASE_PATH + "/be/kwakeroni");
        @AddToClasspath
        private Path path = Paths.get(BASE_PATH + "/be/kwakeroni/test/");
        @AddToClasspath
        private String string = BASE_URL + "/be/kwakeroni/test/extension";

        @Test
        public void replacesContextClassLoaderDuringTest() {
            ClassLoader loader = Thread.currentThread().getContextClassLoader();
            assertThat(loader).isNotSameAs(originalContextClassLoader)
                    .isInstanceOf(URLClassLoader.class);
            URLClassLoader urlLoader = (URLClassLoader) loader;
            assertThat(
                    Arrays.stream(urlLoader.getURLs())
                            .map(URL::toString))
                    .containsExactly(
                            BASE_URL + "/be",
                            BASE_URL + "/be/kwakeroni/",
                            BASE_URL + "/be/kwakeroni/test/",
                            BASE_URL + "/be/kwakeroni/test/extension"
                    );

        }
    }

    public static class TestExtension implements ParameterResolver {
        @Override
        public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
            return true;
        }

        @Override
        public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
            return null;
        }
    }
}