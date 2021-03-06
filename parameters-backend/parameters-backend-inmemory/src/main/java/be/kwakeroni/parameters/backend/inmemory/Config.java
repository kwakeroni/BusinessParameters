package be.kwakeroni.parameters.backend.inmemory;

import be.kwakeroni.parameters.backend.api.ConfigurationProperty;

import java.nio.file.Path;

@SuppressWarnings("WeakerAccess")
public final class Config {

    public static final ConfigurationProperty<Path> STORAGE_FOLDER = ConfigurationProperty.ofPath("inmemory.storage.folder");
    public static final ConfigurationProperty<Path> DEFINITIONS_FOLDER = ConfigurationProperty.ofPath("inmemory.definitions.folder");

    private Config() {
    }
}
