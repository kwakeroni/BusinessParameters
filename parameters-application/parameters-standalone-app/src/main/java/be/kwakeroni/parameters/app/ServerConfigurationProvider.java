package be.kwakeroni.parameters.app;

import be.kwakeroni.parameters.backend.api.Configuration;
import be.kwakeroni.parameters.backend.api.ConfigurationProvider;
import be.kwakeroni.parameters.core.support.backend.ConfigurationSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Properties;

public class ServerConfigurationProvider implements ConfigurationProvider {

    private static final Logger LOG = LoggerFactory.getLogger(ServerConfigurationProvider.class);

    private static Configuration CONFIGURATION = null;

    static synchronized void setConfiguration(Configuration configuration) {
        if (CONFIGURATION == null) {
            CONFIGURATION = configuration;
        } else {
            throw new IllegalStateException("Configuration was already loaded.");
        }
    }

    static synchronized void setConfiguration(File file) {
        setConfiguration(fromFile(file));
    }

    static synchronized void clear() {
        CONFIGURATION = null;
    }

    private static synchronized Configuration ensureLoadedConfiguration() {
        if (CONFIGURATION == null) {
            setConfiguration(fromFile("./business-parameters.properties"));
        }
        if (CONFIGURATION == null) {
            setConfiguration(fromFile("./config/business-parameters.properties"));
        }
        if (CONFIGURATION == null) {
            setConfiguration(fromClasspath("business-parameters.properties"));
        }
        if (CONFIGURATION == null) {
            LOG.warn("Unable to locate configuration file. Continuing without explicit server configuration.");
            setConfiguration(ConfigurationSupport.of(new Properties()));
        }

        return CONFIGURATION;
    }

    private static Configuration fromFile(String path) {
        return fromFile(new File(path));
    }

    private static Configuration fromFile(File file) {
        if (file.exists()) {
            try {
                return ConfigurationSupport.ofPropertiesFile(file);
            } catch (IOException exc) {
                LOG.error("Failed to load log file at {}", file.getAbsolutePath(), exc);
                return null;
            }
        }
        return null;
    }

    private static Configuration fromClasspath(String path) {
        URL url = Thread.currentThread().getContextClassLoader().getResource(path);
        if (url != null) {
            try {
                return ConfigurationSupport.ofPropertiesFile(url);
            } catch (IOException exc) {
                LOG.error("Failed to load log file at {}", url, exc);
                return null;
            }
        }
        return null;
    }


    @Override
    public Configuration getConfiguration() {
        return ensureLoadedConfiguration();
    }
}
