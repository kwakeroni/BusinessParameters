package be.kwakeroni.evelyn.storage.impl;

import be.kwakeroni.evelyn.storage.StorageExistsException;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.Charset;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Collections;
import java.util.stream.Stream;

public final class FileStorage extends StorageSupport {

    private final Path location;

    public FileStorage(Path location) {
        this.location = location;
    }

    @Override
    public String getReference() {
        return location.toUri().toString();
    }

    @Override
    protected void initialize() throws StorageExistsException {
        try {
            Files.createFile(location);
        } catch (FileAlreadyExistsException exc) {
            throw new StorageExistsException("Storage already exists: " + exc.getMessage());
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public void append(String data) {
        try {
            Files.write(location, Collections.singleton(data), StandardOpenOption.APPEND);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public Stream<String> read(Charset charset) {
        try {
            return Files.lines(location, charset);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
