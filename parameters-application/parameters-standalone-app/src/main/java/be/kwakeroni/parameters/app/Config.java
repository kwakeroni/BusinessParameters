package be.kwakeroni.parameters.app;

import be.kwakeroni.parameters.backend.api.ConfigurationProperty;

import java.nio.file.Path;

@SuppressWarnings("WeakerAccess")
public final class Config {

    public static final ConfigurationProperty<Path> WORK_DIRECTORY = ConfigurationProperty.ofPath("server.workDir");
    public static final ConfigurationProperty<Integer> PORT = ConfigurationProperty.ofInteger("server.port");
    public static final ConfigurationProperty<String> CONTEXT_PATH = ConfigurationProperty.ofString("server.contextPath");

    private Config() {
    }
}
