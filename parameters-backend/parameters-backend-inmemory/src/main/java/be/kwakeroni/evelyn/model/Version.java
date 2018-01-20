package be.kwakeroni.evelyn.model;

import be.kwakeroni.evelyn.model.parser.ParseException;
import be.kwakeroni.evelyn.model.versions.DatabaseAccessorSupport;
import be.kwakeroni.evelyn.storage.Storage;
import be.kwakeroni.evelyn.storage.StorageExistsException;
import be.kwakeroni.evelyn.storage.StorageProvider;

import java.util.Arrays;
import java.util.Objects;

public enum Version implements DatabaseProvider {
    V0_1("0.1");

    private final String number;

    private Version(String number) {
        this.number = number;
    }

    public String getVersionNumber() {
        return this.number;
    }

    @Override
    public DatabaseAccessor create(StorageProvider storageProvider, String databaseName) throws DatabaseException {
        Storage storage;
        try {
            storage = storageProvider.create(databaseName);
            return new DatabaseAccessorSupport(this.number, databaseName, storage);
        } catch (StorageExistsException exc) {
            throw new DatabaseException("Could not create database " + databaseName, exc);
        }
    }

    @Override
    public DatabaseAccessor read(Storage storage) throws DatabaseException {
        try {
            return DatabaseAccessorSupport.createFrom(storage);
        } catch (ParseException exc) {
            throw new DatabaseException(exc.getMessage(), exc);
        }
    }


    public static Version byNumber(String number) {
        Objects.requireNonNull(number, "number");
        return Arrays.stream(Version.values())
                .filter(version -> number.equals(version.getVersionNumber()))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("Unsupported version: " + number));
    }
}
