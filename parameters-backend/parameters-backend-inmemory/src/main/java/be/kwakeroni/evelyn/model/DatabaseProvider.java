package be.kwakeroni.evelyn.model;

import be.kwakeroni.evelyn.storage.Storage;
import be.kwakeroni.evelyn.storage.StorageProvider;

public interface DatabaseProvider {

    public DatabaseAccessor create(StorageProvider storage, String databaseName) throws DatabaseException;

    public DatabaseAccessor read(Storage storage) throws DatabaseException;

}
