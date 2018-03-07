package be.kwakeroni.evelyn.model;

import be.kwakeroni.evelyn.model.impl.Version;
import be.kwakeroni.evelyn.storage.Storage;
import be.kwakeroni.evelyn.storage.StorageProvider;

public class DefaultDatabaseProvider implements DatabaseProvider {

    private static final DefaultDatabaseProvider INSTANCE = new DefaultDatabaseProvider();

    public static DatabaseProvider getInstance() {
        return INSTANCE;
    }

    DefaultDatabaseProvider() {

    }

    @Override
    public DatabaseAccessor create(StorageProvider storage, String databaseName) throws DatabaseException {
        return getDefaultProvider().create(storage, databaseName);
    }

    @Override
    public DatabaseAccessor read(Storage storage) throws DatabaseException {
        try {
            String number = storage.readVersion();
            DatabaseProvider provider = getProviderByVersion(number);
            return provider.read(storage);
        } catch (ParseException exc) {
            throw new DatabaseException("Could not read version of database", exc);
        }
    }

    DatabaseProvider getProviderByVersion(String version) {
        return Version.byNumber(version);
    }

    DatabaseProvider getDefaultProvider() {
        return Version.LATEST;
    }
}
