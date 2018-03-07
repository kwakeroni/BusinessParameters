package be.kwakeroni.parameters.backend.inmemory.persistence;

import be.kwakeroni.evelyn.model.DatabaseAccessor;
import be.kwakeroni.evelyn.model.DatabaseException;
import be.kwakeroni.evelyn.model.DatabaseProvider;
import be.kwakeroni.evelyn.storage.StorageProvider;
import be.kwakeroni.parameters.backend.inmemory.api.GroupData;
import be.kwakeroni.parameters.backend.inmemory.api.InMemoryGroup;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.extension.mockito.MockitoExtension;
import org.mockito.Mock;

import java.time.LocalDateTime;
import java.util.stream.Stream;

import static be.kwakeroni.evelyn.test.TestModel.event;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
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
        when(databaseProvider.readCreate(any(), anyString())).thenReturn(accessor);
        when(accessor.getData()).thenAnswer(__ -> Stream.of(
                event("none", "1", "ADD", "{\"param\":\"value\"}", LocalDateTime.now()),
                event("none", "2", "ADD", "{\"param\":\"value\"}", LocalDateTime.now()),
                event("none", "1", "REPLACE", "{\"param\":\"value\"}", LocalDateTime.now())));

        GroupData groupData = dataStore.getGroupData(group);

        assertThat(groupData).isInstanceOf(PersistedGroupData.class);
        assertThat(groupData.getGroup()).isSameAs(group);
        assertThat(groupData.getEntries()).hasSize(2);

        verify(databaseProvider).readCreate(storageProvider, "test.new.data");
    }

    @Test
    @DisplayName("Propagates Database Exception")
    void testCreateGroupDataException() throws Exception {
        DatabaseException exception = new DatabaseException("Test");
        when(databaseProvider.readCreate(any(), any())).thenThrow(exception);

        assertThatThrownBy(() -> dataStore.getGroupData(group))
                .hasCause(exception);
    }

}