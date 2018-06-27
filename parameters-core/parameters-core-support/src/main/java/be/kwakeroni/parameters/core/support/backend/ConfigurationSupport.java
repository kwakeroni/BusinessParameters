package be.kwakeroni.parameters.core.support.backend;

import be.kwakeroni.parameters.backend.api.Configuration;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Optional;
import java.util.Properties;

public class ConfigurationSupport implements Configuration {

    private final Properties properties;

    private ConfigurationSupport(Properties properties) {
        this.properties = properties;
    }

    @Override
    public Optional<String> getStringParameter(String name) {
        return Optional.ofNullable(properties.getProperty(name));
    }

    @SuppressWarnings("WeakerAccess")
    public static Configuration of(Properties properties) {
        Properties copy = new Properties();
        for (String property : properties.stringPropertyNames()) {
            copy.setProperty(property, properties.getProperty(property));
        }
        return new ConfigurationSupport(copy);
    }


    @SuppressWarnings("WeakerAccess")
    public static Configuration ofPropertiesFile(InputStream stream) throws IOException {
        Properties properties = new Properties();
        properties.load(stream);
        return new ConfigurationSupport(properties);
    }

    @SuppressWarnings("WeakerAccess")
    public static Configuration ofPropertiesFile(URL url) throws IOException {
        try (InputStream stream = url.openStream()) {
            return ofPropertiesFile(stream);
        }
    }

    @SuppressWarnings("WeakerAccess")
    public static Configuration ofPropertiesFile(File file) throws IOException {
        try (InputStream stream = new FileInputStream(file)) {
            return ofPropertiesFile(stream);
        }
    }

}
