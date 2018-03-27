package be.kwakeroni.it;

import be.kwakeroni.evelyn.client.CachingClientTable;
import be.kwakeroni.evelyn.client.ClientTable;
import be.kwakeroni.evelyn.client.DefaultClientTable;
import be.kwakeroni.evelyn.model.DatabaseAccessor;
import be.kwakeroni.evelyn.model.impl.Version;
import be.kwakeroni.evelyn.storage.Storage;
import be.kwakeroni.evelyn.storage.StorageProvider;
import be.kwakeroni.evelyn.storage.impl.TestStorageSupport;
import be.kwakeroni.parameters.backend.api.query.BackendQuery;
import be.kwakeroni.parameters.backend.api.query.BackendWireFormatterContext;
import be.kwakeroni.parameters.backend.inmemory.api.EntryData;
import be.kwakeroni.parameters.backend.inmemory.api.GroupData;
import be.kwakeroni.parameters.backend.inmemory.api.InMemoryGroup;
import be.kwakeroni.parameters.backend.inmemory.api.InMemoryQuery;
import be.kwakeroni.parameters.backend.inmemory.persistence.GroupTableOperation;
import be.kwakeroni.parameters.backend.inmemory.persistence.PersistedGroupData;
import be.kwakeroni.parameters.backend.inmemory.support.DefaultEntryData;
import be.kwakeroni.parameters.definition.api.ParameterGroupDefinition;
import be.kwakeroni.test.factory.TestMap;
import org.junit.jupiter.api.Test;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class PersistedGroupDataIT {


    @Test
    void testBlankDB() throws Exception {
        InMemoryGroup group = testGroup();

        PersistedGroupData data = new PersistedGroupData(group, createTable(group));

        EntryData tom = entry("Tom", 20);
        EntryData lance = entry("Lance", 30);
        data.addEntry(tom);
        assertThat(data.getEntries().map(entry -> entry.getValue("name"))).containsOnly("Tom");
        data.addEntry(lance);
        assertThat(data.getEntries().map(entry -> entry.getValue("name"))).containsOnly("Tom", "Lance");
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
        StorageProvider storageProvider = mockStorageProvider();
        DatabaseAccessor accessor = Version.V0_1.create(storageProvider, group.getName());
        ClientTable<EntryData> delegate = new DefaultClientTable<>(accessor, GroupTableOperation::valueOf);
        return new CachingClientTable<EntryData>(accessor, GroupTableOperation::valueOf, EntryData::getId);
    }

    private static StorageProvider mockStorageProvider() {
        return new StorageProvider() {
            @Override
            public Storage create(String name) {
                return mockStorage(name);
            }

            @Override
            public Storage read(String name) {
                return null;
            }

            @Override
            public boolean exists(String name) {
                return false;
            }
        };
    }

    private static Storage mockStorage(String name) {
        return new TestStorageSupport() {

            private List<String> list;

            @Override
            protected void initialize() {
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

    private static InMemoryGroup testGroup() {
        return new InMemoryGroup() {
            @Override
            public EntryData validateNewEntry(EntryData entry, GroupData storage) {
                return entry;
            }

            @Override
            public String getName() {
                return "myGroup";
            }

            @Override
            public ParameterGroupDefinition<?> getDefinition() {
                throw new UnsupportedOperationException();
            }

            @Override
            public BackendQuery<? extends InMemoryQuery<?>, ?> internalize(Object query, BackendWireFormatterContext context) {
                throw new UnsupportedOperationException();
            }
        };
    }

}
