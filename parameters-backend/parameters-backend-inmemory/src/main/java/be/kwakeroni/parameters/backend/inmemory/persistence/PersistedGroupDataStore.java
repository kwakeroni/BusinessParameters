package be.kwakeroni.parameters.backend.inmemory.persistence;

import be.kwakeroni.evelyn.client.ClientTable;
import be.kwakeroni.evelyn.client.DefaultClientTable;
import be.kwakeroni.evelyn.model.DatabaseAccessor;
import be.kwakeroni.evelyn.model.DatabaseException;
import be.kwakeroni.evelyn.model.DatabaseProvider;
import be.kwakeroni.evelyn.storage.StorageProvider;
import be.kwakeroni.parameters.backend.inmemory.api.EntryData;
import be.kwakeroni.parameters.backend.inmemory.api.GroupData;
import be.kwakeroni.parameters.backend.inmemory.api.InMemoryGroup;
import be.kwakeroni.parameters.backend.inmemory.service.GroupDataStore;

public class PersistedGroupDataStore implements GroupDataStore {

    private final StorageProvider storageProvider;
    private final DatabaseProvider provider;

    public PersistedGroupDataStore(StorageProvider storageProvider, DatabaseProvider provider) {
        this.storageProvider = storageProvider;
        this.provider = provider;
    }

    @Override
    public GroupData getGroupData(InMemoryGroup group) {
        return new PersistedGroupData(group, createTable(group));
    }

    private ClientTable<EntryData> createTable(InMemoryGroup group) {
        DatabaseAccessor accessor;
        try {
            accessor = this.provider.readCreate(storageProvider, group.getName());
            return new DefaultClientTable<>(accessor, GroupTableOperation::valueOf);
        } catch (DatabaseException exc) {
            throw new IllegalStateException(exc);
        }
    }

}
