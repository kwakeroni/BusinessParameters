package be.kwakeroni.evelyn.storage.impl;

import be.kwakeroni.evelyn.storage.Storage;
import be.kwakeroni.evelyn.storage.StorageExistsException;
import be.kwakeroni.evelyn.storage.StorageProvider;

import java.nio.file.Files;
import java.nio.file.Path;

public class FileStorageProvider implements StorageProvider {

    private final Path storageDirectory;

    public FileStorageProvider(Path storageDirectory) {
        this.storageDirectory = storageDirectory;
    }

    @Override
    public Storage create(String name) throws StorageExistsException {
        Path path = getStorageFile(name);
        if (Files.exists(path)) {
            throw new StorageExistsException("Storage file already exists: " + path.toAbsolutePath());
        }
        return new FileStorage(path);
    }

    @Override
    public Storage read(String name) {
        Path path = getStorageFile(name);
        if (Files.notExists(path)) {
            throw new IllegalStateException("Storage file not found:" + path.toAbsolutePath());
        }
        return new FileStorage(path);
    }

    @Override
    public boolean exists(String name) {
        return Files.exists(getStorageFile(name));
    }

    private Path getStorageFile(String name) {
        return storageDirectory.resolve(name + ".edb");
    }

}
