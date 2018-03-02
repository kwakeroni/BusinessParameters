package be.kwakeroni.it;

import be.kwakeroni.evelyn.client.ClientTable;
import be.kwakeroni.evelyn.model.DatabaseAccessor;
import be.kwakeroni.evelyn.model.Version;
import be.kwakeroni.evelyn.storage.Storage;
import be.kwakeroni.evelyn.storage.StorageExistsException;
import be.kwakeroni.evelyn.storage.StorageProvider;
import be.kwakeroni.evelyn.storage.impl.TestStorageSupport;
import be.kwakeroni.parameters.backend.inmemory.api.EntryData;
import be.kwakeroni.parameters.backend.inmemory.api.InMemoryGroup;
import be.kwakeroni.parameters.backend.inmemory.persistence.GroupTableFactory;
import be.kwakeroni.parameters.backend.inmemory.persistence.PersistedGroupData;
import be.kwakeroni.parameters.backend.inmemory.support.DefaultEntryData;
import be.kwakeroni.test.TestMap;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

public class PersistedGroupDataIT {


    public void testBlankDB() throws Exception {
        InMemoryGroup group = null; // new InmemoryMappedGroup("name", null, new InmemorySimpleGroup("test", null, "name", "age"));
        ClientTable<EntryData> table = createTable(group);


        PersistedGroupData data = new PersistedGroupData(group, table);

        EntryData tom = entry("Tom", 20);
        EntryData lance = entry("Lance", 30);
        data.addEntry(tom);
        data.addEntry(lance);
        data.modifyEntry(tom, entry -> entry.setValue("age", "22"));
        data.addEntry(entry("With\\backslash", 1));
        data.addEntry(entry("With\"quotes\"", 2));
        data.addEntry(entry("With|pipe", 3));
        data.addEntry(entry("With\r\nnewline", 4));
        data.addEntry(entry("Mixed | escaped \r\n \"phrase\"", 4));

        assertThat(data.getEntries().map(entry -> entry.getValue("name"))).containsOnly(
                "Tom",
                "Lance",
                "With\\backslash",
                "With\"quotes\"",
                "With|pipe",
                "With\r\nnewline",
                "Mixed | escaped \r\n \"phrase\""
        );
    }

    private static ClientTable<EntryData> createTable(InMemoryGroup group) throws Exception {
        StorageProvider storageProvider = PersistedGroupDataIT::mockStorage;
        DatabaseAccessor accessor = Version.V0_1.create(storageProvider, group.getName());
        accessor.createDatabase();
        return new GroupTableFactory(groupName -> accessor).createTable(group);
    }

    private static Storage mockStorage(String name) {
        return new TestStorageSupport() {

            private List<String> list;

            @Override
            protected void initialize() throws StorageExistsException {
                this.list = new ArrayList<>();
            }

            @Override
            public String getReference() {
                return this.toString() + '[' + name + ']';
            }

            @Override
            public void append(String data) {
                list.add(data);
            }

            @Override
            public Stream<String> read(Charset charset) {
                return list.stream();
            }
        };
    }

    private static EntryData entry(String name, int age) {
        return DefaultEntryData.of(TestMap.of("name", name, "age", String.valueOf(age)));
    }

}
