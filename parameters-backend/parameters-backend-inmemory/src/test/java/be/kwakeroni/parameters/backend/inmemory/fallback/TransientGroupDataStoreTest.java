package be.kwakeroni.parameters.backend.inmemory.fallback;

import be.kwakeroni.parameters.backend.inmemory.api.EntryData;
import be.kwakeroni.parameters.backend.inmemory.api.GroupData;
import be.kwakeroni.parameters.backend.inmemory.api.InMemoryGroup;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.extension.mockito.MockitoExtension;
import org.mockito.Mock;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TransientGroupDataStoreTest {

    private TransientGroupDataStore dataStore = new TransientGroupDataStore();

    @Test
    @DisplayName("Produces transient group data")
    void testCreateTransientGroupData(@Mock InMemoryGroup group) {
        GroupData data = dataStore.getGroupData(group);

        assertThat(data).isInstanceOf(TransientGroupData.class);
        assertThat(data.getGroup()).isSameAs(group);
        assertThat(data.getEntries()).isEmpty();
    }

    @Test
    @DisplayName("Allows external data initialization")
    void testCreateTransientGroupDataExternalInit(@Mock InMemoryGroup group, @Mock EntryData data1, @Mock EntryData data2) {

        when(group.getName()).thenReturn("myGroup");
        dataStore.setEntries("myGroup", Arrays.asList(data1, data2));

        GroupData data = dataStore.getGroupData(group);

        assertThat(data.getEntries()).containsOnly(data1, data2);
    }

    @Test
    @DisplayName("Uses default group data initialization")
    void testCreateTransientGroupDataGroupInit(@Mock InMemoryGroup group, @Mock EntryData data1, @Mock EntryData data2) {

        when(group.initialData()).thenReturn(Arrays.asList(data1, data2));

        GroupData data = dataStore.getGroupData(group);

        assertThat(data.getEntries()).containsOnly(data1, data2);
    }


}