package be.kwakeroni.scratch.env.inmemory;

import be.kwakeroni.evelyn.client.ClientTable;
import be.kwakeroni.evelyn.client.DefaultClientTable;
import be.kwakeroni.evelyn.model.DatabaseException;
import be.kwakeroni.evelyn.model.DefaultDatabaseProvider;
import be.kwakeroni.evelyn.storage.Storage;
import be.kwakeroni.evelyn.storage.StorageExistsException;
import be.kwakeroni.evelyn.storage.StorageProvider;
import be.kwakeroni.evelyn.storage.impl.FileStorageProvider;
import be.kwakeroni.parameters.backend.api.factory.BusinessParametersBackendFactory;
import be.kwakeroni.parameters.backend.inmemory.api.EntryData;
import be.kwakeroni.parameters.backend.inmemory.factory.InMemoryBackendServiceFactory;
import be.kwakeroni.parameters.backend.inmemory.persistence.GroupTableOperation;
import be.kwakeroni.parameters.backend.inmemory.persistence.PersistedGroupDataStore;
import be.kwakeroni.scratch.env.TestData;
import be.kwakeroni.scratch.tv.AbstractMappedRangedTVGroup;
import be.kwakeroni.scratch.tv.AbstractRangedTVGroup;
import be.kwakeroni.scratch.tv.Dag;
import be.kwakeroni.scratch.tv.MappedRangedFilterTVGroup;
import be.kwakeroni.scratch.tv.MappedRangedQueryTVGroup;
import be.kwakeroni.scratch.tv.MappedTVGroup;
import be.kwakeroni.scratch.tv.RangedFilterTVGroup;
import be.kwakeroni.scratch.tv.RangedQueryTVGroup;
import be.kwakeroni.scratch.tv.SimpleTVGroup;
import be.kwakeroni.scratch.tv.Slot;
import org.json.JSONObject;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class PersistedInMemoryTestData implements TestData {

    private final TemporaryFolder folder = new TemporaryFolder();
    private final PersistedGroupDataStore dataStore;
    private final List<String> groups = new ArrayList<>();
    private StorageProvider fileStorage;

    public PersistedInMemoryTestData() {
        this.dataStore = new PersistedGroupDataStore(storageProvider());
        InMemoryBackendServiceFactory.setDataStoreSupplier(() -> this.dataStore);
        try {
            this.folder.create();
        } catch (IOException exc) {
            throw new UncheckedIOException(exc);
        }
        reset();
    }

    @Override
    public boolean acceptBackend(BusinessParametersBackendFactory factory) {
        return factory instanceof InMemoryBackendServiceFactory;
    }

    @Override
    public boolean hasDataForGroup(String name) {
        return this.groups.contains(name);
    }

    private void addGroupData(String group1Name, String group2Name, EntryData... data) {
        addGroupData(group1Name, Arrays.asList(data));
        addGroupData(group2Name, Arrays.asList(data));
    }

    private void addGroupData(String groupName, EntryData... data) {
        addGroupData(groupName, Arrays.asList(data));
    }

    private void addGroupData(String groupName, Collection<EntryData> data) {
        this.groups.add(groupName);
        ClientTable<EntryData> table = null;
        try {
            table = new DefaultClientTable<>(DefaultDatabaseProvider.getInstance().create(fileStorage, groupName), GroupTableOperation::valueOf);
        } catch (DatabaseException exc) {
            throw new RuntimeException(exc);
        }

        for (EntryData entry : data) {
            table.append("anonymous", "ADD", entry.getId(), new JSONObject(entry.asMap()).toString());
        }

    }


    @Override
    public void notifyModifiedGroup(String name) {

    }

    @Override
    public void reset() {
        try {
            File newFolder = this.folder.newFolder();
            this.fileStorage = new FileStorageProvider(newFolder.toPath());
        } catch (IOException exc) {
            throw new UncheckedIOException(exc);
        }

        addGroupData(SimpleTVGroup.instance().getName(),
                SimpleTVGroup.getEntryData(Dag.MAANDAG, Slot.atHour(20)));

        addGroupData(MappedTVGroup.instance().getName(),
                MappedTVGroup.entryData(Dag.ZATERDAG, "Samson"),
                MappedTVGroup.entryData(Dag.ZONDAG, "Morgen Maandag"));

        addGroupData(RangedFilterTVGroup.instance().getName(), RangedQueryTVGroup.instance().getName(),
                AbstractRangedTVGroup.entryData(Slot.atHour(8), Slot.atHour(12), "Samson"),
                AbstractRangedTVGroup.entryData(Slot.atHalfPast(20), Slot.atHour(22), "Morgen Maandag"));

        addGroupData(MappedRangedFilterTVGroup.instance().getName(), MappedRangedQueryTVGroup.instance().getName(),
                AbstractMappedRangedTVGroup.entryData(Dag.MAANDAG, Slot.atHalfPast(20), Slot.atHour(22), "Gisteren Zondag"),
                AbstractMappedRangedTVGroup.entryData(Dag.ZATERDAG, Slot.atHour(8), Slot.atHour(12), "Samson"),
                AbstractMappedRangedTVGroup.entryData(Dag.ZATERDAG, Slot.atHour(14), Slot.atHour(18), "Koers"),
                AbstractMappedRangedTVGroup.entryData(Dag.ZONDAG, Slot.atHalfPast(20), Slot.atHour(22), "Morgen Maandag")
        );

    }

    @Override
    public void close() throws Exception {
        this.folder.delete();
    }

    private StorageProvider storageProvider() {
        return new StorageProvider() {
            @Override
            public Storage create(String name) throws StorageExistsException {
                return fileStorage.create(name);
            }

            @Override
            public Storage read(String name) {
                return fileStorage.read(name);
            }

            @Override
            public boolean exists(String name) {
                return fileStorage.exists(name);
            }
        };
    }
}
