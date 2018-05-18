package be.kwakeroni.parameters.backend.inmemory.persistence;

import be.kwakeroni.evelyn.model.DatabaseAccessor;
import be.kwakeroni.evelyn.model.DatabaseException;
import be.kwakeroni.evelyn.model.DatabaseProvider;
import be.kwakeroni.evelyn.model.Event;
import be.kwakeroni.evelyn.storage.Storage;
import be.kwakeroni.evelyn.storage.StorageProvider;
import be.kwakeroni.parameters.backend.inmemory.api.GroupData;
import be.kwakeroni.parameters.backend.inmemory.api.InMemoryGroup;
import be.kwakeroni.parameters.backend.inmemory.support.DefaultEntryData;
import be.kwakeroni.test.factory.TestMap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.extension.mockito.MockitoExtension;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.stream.Stream;

import static be.kwakeroni.evelyn.test.TestModel.event;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PersistedGroupDataStoreTest {

    @Mock
    private InMemoryGroup group;
    @Mock
    private StorageProvider storageProvider;
    @Mock
    private DatabaseProvider databaseProvider;

    private PersistedGroupDataStore dataStore;

    @BeforeEach
    void setUp() {
        this.dataStore = new PersistedGroupDataStore(storageProvider, databaseProvider);
    }

    @Test
    @DisplayName("Creates persistent group data")
    void testCreateGroupData(@Mock DatabaseAccessor accessor) throws Exception {

        when(group.getName()).thenReturn("test.new.data");
        when(storageProvider.exists(any())).thenReturn(false);
        when(databaseProvider.create(any(), anyString())).thenReturn(accessor);

        GroupData groupData = dataStore.getGroupData(group);

        assertThat(groupData).isInstanceOf(PersistedGroupData.class);
        assertThat(groupData.getGroup()).isSameAs(group);
        assertThat(groupData.getEntries()).hasSize(0);

        verify(databaseProvider).create(storageProvider, "test.new.data");
    }


    @Test
    @DisplayName("Adds initial entries to created group data")
    void testAddInitialData(@Mock DatabaseAccessor accessor) throws Exception {

        when(group.getName()).thenReturn("test.new.data");
        when(storageProvider.exists(any())).thenReturn(false);
        when(databaseProvider.create(any(), anyString())).thenReturn(accessor);
        when(group.validateNewEntry(any(), any())).then(returnsFirstArg());
        when(group.initialData()).thenReturn(Arrays.asList(
                DefaultEntryData.of("id1", TestMap.of("param", "value1")),
                DefaultEntryData.of("id2", TestMap.of("param", "value2"))
        ));

        GroupData groupData = dataStore.getGroupData(group);

        assertThat(groupData).isInstanceOf(PersistedGroupData.class);
        assertThat(groupData.getGroup()).isSameAs(group);

        verify(databaseProvider).create(storageProvider, "test.new.data");
        ArgumentCaptor<Event> events = ArgumentCaptor.forClass(Event.class);
        verify(accessor, times(2)).append(events.capture());
        Event event1 = events.getAllValues().get(0);
        Event event2 = events.getAllValues().get(1);

        assertThat(event1.getObjectId()).isEqualTo("id1");
        assertThat(event1.getOperation()).isEqualTo("ADD");
        assertThat(event1.getData()).isEqualTo("{\"param\":\"value1\"}");
        assertThat(event2.getObjectId()).isEqualTo("id2");
        assertThat(event2.getOperation()).isEqualTo("ADD");
        assertThat(event2.getData()).isEqualTo("{\"param\":\"value2\"}");

    }


    @Test
    @DisplayName("Reads persistent group data")
    void testReadGroupData(@Mock Storage storage, @Mock DatabaseAccessor accessor) throws Exception {

        when(group.getName()).thenReturn("test.new.data");
        when(storageProvider.exists(any())).thenReturn(true);
        when(storageProvider.read(any())).thenReturn(storage);
        when(databaseProvider.read(any())).thenReturn(accessor);
        when(accessor.getData()).thenAnswer(__ -> Stream.of(
                event("none", "1", "ADD", "{\"param\":\"value\"}", LocalDateTime.now()),
                event("none", "2", "ADD", "{\"param\":\"value\"}", LocalDateTime.now()),
                event("none", "1", "REPLACE", "{\"param\":\"value\"}", LocalDateTime.now())));

        GroupData groupData = dataStore.getGroupData(group);

        assertThat(groupData).isInstanceOf(PersistedGroupData.class);
        assertThat(groupData.getGroup()).isSameAs(group);
        assertThat(groupData.getEntries()).hasSize(2);

        verify(storageProvider).read("test.new.data");
        verify(databaseProvider).read(storage);
    }


    @Test
    @DisplayName("Propagates Database Exception")
    void testCreateGroupDataException() throws Exception {
        when(group.getName()).thenReturn("test.new.data");
        when(storageProvider.exists(any())).thenReturn(false);
        DatabaseException exception = new DatabaseException("Test");
        when(databaseProvider.create(any(), any())).thenThrow(exception);

        assertThatThrownBy(() -> dataStore.getGroupData(group))
                .hasCause(exception);
    }

}