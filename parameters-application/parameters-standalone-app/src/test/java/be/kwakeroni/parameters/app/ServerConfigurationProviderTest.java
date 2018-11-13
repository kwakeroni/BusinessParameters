package be.kwakeroni.parameters.app;

import be.kwakeroni.parameters.backend.api.Configuration;
import be.kwakeroni.parameters.backend.api.ConfigurationProvider;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.extension.mockito.MockitoExtension;
import org.junit.rules.TemporaryFolder;
import org.mockito.Mock;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.ServiceLoader;
import java.util.Set;
import java.util.function.UnaryOperator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith(MockitoExtension.class)
class ServerConfigurationProviderTest {

    @AfterEach
    void cleanup() {
        ServerConfigurationProvider.clear();
    }

    @Test
    @DisplayName("Provides an empty configuration")
    void testEmptyConfig() {
        Configuration config = new ServerConfigurationProvider().getConfiguration();
        assertThat(config.getStringParameter("property")).isNotPresent();
    }

    @Test
    @DisplayName("Provides preset configuration")
    void testPresetConfig() throws Exception {
        TemporaryFolder folder = new TemporaryFolder();
        try {
            folder.create();
            File preset = new File(folder.getRoot(), "preset");
            Files.write(preset.toPath(), Collections.singleton("property=testPresetConfig"));

            ServerConfigurationProvider.setConfiguration(preset);

            Configuration config = new ServerConfigurationProvider().getConfiguration();
            assertThat(config.getStringParameter("property")).hasValue("testPresetConfig");
        } finally {
            folder.delete();
        }
    }

    @Test
    @DisplayName("Provides configuration from properties file")
    void testFileConfig() throws Exception {
        Path file = Paths.get("business-parameters.properties");
        try {
            Files.write(file, Collections.singleton("property=testFileConfig"));

            Configuration config = new ServerConfigurationProvider().getConfiguration();
            assertThat(config.getStringParameter("property")).hasValue("testFileConfig");
        } finally {
            Files.deleteIfExists(file);
        }
    }

    @Test
    @DisplayName("Survives unreadable properties file")
    void testFileConfigUnreadable() throws Exception {
        Path file = Paths.get("business-parameters.properties");
        try {
            Files.write(file, Collections.singleton("property=testFileConfigUnreadable"));
            Files.setPosixFilePermissions(file, PosixFilePermissions.fromString("---------"));

            Configuration config = new ServerConfigurationProvider().getConfiguration();
            assertThat(config.getStringParameter("property")).isNotPresent();
        } finally {
            Files.deleteIfExists(file);
        }
    }

    @Test
    @DisplayName("Provides configuration from properties file in config folder")
    void testFileInConfigConfig() throws Exception {
        Path configFolder = Paths.get("config");
        Path file = configFolder.resolve("business-parameters.properties");
        try {
            Files.createDirectories(configFolder);
            Files.write(file, Collections.singleton("property=testFileInConfigConfig"));

            Configuration config = new ServerConfigurationProvider().getConfiguration();
            assertThat(config.getStringParameter("property")).hasValue("testFileInConfigConfig");
        } finally {
            Files.deleteIfExists(file);
            Files.deleteIfExists(configFolder);
        }
    }

    @Test
    @DisplayName("Provides configuration from properties file on classpath")
    void testClasspathConfig() {
        String pathPrefix = this.getClass().getName().replace('.', '/') + "/testClasspathConfig/";
        ClassLoader original = Thread.currentThread().getContextClassLoader();
        try {
            UnaryOperator<String> rewrite = resource -> pathPrefix + resource;
            Thread.currentThread().setContextClassLoader(rewriteResourceLoader(original, rewrite));

            Configuration config = new ServerConfigurationProvider().getConfiguration();
            assertThat(config.getStringParameter("property")).hasValue("testClasspathConfig");

        } finally {
            Thread.currentThread().setContextClassLoader(original);
        }
    }

    @Test
    @DisplayName("Survives unreadable properties file on classpath")
    void testClasspathConfigUnreadable() throws Exception {
        String pathPrefix = this.getClass().getName().replace('.', '/') + "/testClasspathConfigUnreadable/";
        ClassLoader original = Thread.currentThread().getContextClassLoader();
        Path path = Paths.get(original.getResource(pathPrefix + "business-parameters.properties").toURI());
        Set<PosixFilePermission> originalPermissions = Files.getPosixFilePermissions(path);

        try {
            Files.setPosixFilePermissions(path, PosixFilePermissions.fromString("---------"));

            UnaryOperator<String> rewrite = resource -> pathPrefix + resource;
            Thread.currentThread().setContextClassLoader(rewriteResourceLoader(original, rewrite));

            Configuration config = new ServerConfigurationProvider().getConfiguration();
            assertThat(config.getStringParameter("property")).isNotPresent();

        } finally {
            Thread.currentThread().setContextClassLoader(original);
            Files.setPosixFilePermissions(path, originalPermissions);
        }
    }

    @Test
    @DisplayName("Disallows setting configuration multiple times")
    void testDisallowMultipleConfigurations(@Mock Configuration configuration) {
        ServerConfigurationProvider.setConfiguration(configuration);
        assertThatThrownBy(() -> ServerConfigurationProvider.setConfiguration(configuration))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    @DisplayName("Is exposed as a service")
    void testExposedService(@Mock Configuration configuration) {
        ServerConfigurationProvider.setConfiguration(configuration);

        Iterator<ConfigurationProvider> providers = ServiceLoader.load(ConfigurationProvider.class).iterator();

        assertThat(providers.hasNext()).isTrue();

        ConfigurationProvider provider = providers.next();

        assertThat(providers.hasNext()).isFalse();
        assertThat(provider).isInstanceOf(ServerConfigurationProvider.class);
        assertThat(provider.getConfiguration()).isSameAs(configuration);
    }

    private static ClassLoader rewriteResourceLoader(ClassLoader parent, UnaryOperator<String> rewrite) {
        return new ClassLoader(parent) {
            @Override
            public URL getResource(String name) {
                URL result = super.getResource(rewrite(name));
                return (result != null) ? result : super.getResource(name);
            }

            @Override
            public Enumeration<URL> getResources(String name) throws IOException {
                Enumeration<URL> result = super.getResources(rewrite(name));
                return (result.hasMoreElements()) ? result : super.getResources(name);
            }

            @Override
            public InputStream getResourceAsStream(String name) {
                InputStream result = super.getResourceAsStream(rewrite(name));
                return (result != null) ? result : super.getResourceAsStream(name);
            }

            private String rewrite(String name) {
                return rewrite.apply(name);
            }
        };
    }

}
