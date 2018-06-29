package be.kwakeroni.test.util;

import be.kwakeroni.parameters.backend.api.Configuration;
import be.kwakeroni.parameters.backend.api.ConfigurationProvider;

public class TestConfigurationProvider implements ConfigurationProvider {

    private static Configuration CONFIGURATION;

    public static void setConfiguration(Configuration configuration) {
        CONFIGURATION = configuration;
    }

    public static void clear() {
        CONFIGURATION = null;
    }

    @Override
    public Configuration getConfiguration() {
        return CONFIGURATION;
    }
}
