package be.kwakeroni.evelyn.model;

import be.kwakeroni.evelyn.storage.Storage;
import be.kwakeroni.evelyn.storage.StorageProvider;

public interface DatabaseProvider {

    public DatabaseAccessor create(StorageProvider storage, String databaseName) throws DatabaseException;

    public default DatabaseAccessor readCreate(StorageProvider storage, String databaseName) throws DatabaseException {
        if (storage.exists(databaseName)) {
            return read(storage.read(databaseName));
        } else {
            return create(storage, databaseName);
        }
    }

    public DatabaseAccessor read(Storage storage) throws DatabaseException;

}
