package be.kwakeroni.parameters.backend.inmemory.persistence;

import be.kwakeroni.evelyn.client.DefaultClientTable;
import be.kwakeroni.evelyn.model.DatabaseAccessor;
import be.kwakeroni.evelyn.model.DatabaseException;
import be.kwakeroni.evelyn.model.DatabaseProvider;
import be.kwakeroni.evelyn.model.DefaultDatabaseProvider;
import be.kwakeroni.evelyn.storage.StorageProvider;
import be.kwakeroni.parameters.backend.inmemory.api.GroupData;
import be.kwakeroni.parameters.backend.inmemory.api.InMemoryGroup;
import be.kwakeroni.parameters.backend.inmemory.service.GroupDataStore;

public class PersistedGroupDataStore implements GroupDataStore {

    private final StorageProvider storageProvider;
    private final DatabaseProvider provider;

    public PersistedGroupDataStore(StorageProvider storageProvider) {
        this(storageProvider, DefaultDatabaseProvider.getInstance());
    }

    public PersistedGroupDataStore(StorageProvider storageProvider, DatabaseProvider provider) {
        this.storageProvider = storageProvider;
        this.provider = provider;
    }

    @Override
    public GroupData getGroupData(InMemoryGroup group) {
        try {
            return getGroupData0(group);
        } catch (DatabaseException exc) {
            throw new IllegalStateException(exc);
        }
    }

    private GroupData getGroupData0(InMemoryGroup group) throws DatabaseException {
        GroupData data;
        String databaseName = group.getName();


        if (storageProvider.exists(databaseName)) {
            DatabaseAccessor accessor = provider.read(storageProvider.read(databaseName));
            data = getGroupData(group, accessor);
        } else {
            DatabaseAccessor accessor = provider.create(storageProvider, databaseName);
            data = getGroupData(group, accessor);
            group.initialData().forEach(data::addEntry);
        }

        return data;
    }

    private GroupData getGroupData(InMemoryGroup group, DatabaseAccessor accessor) {
        return new PersistedGroupData(group, new DefaultClientTable<>(accessor, GroupTableOperation::valueOf));
    }

}
