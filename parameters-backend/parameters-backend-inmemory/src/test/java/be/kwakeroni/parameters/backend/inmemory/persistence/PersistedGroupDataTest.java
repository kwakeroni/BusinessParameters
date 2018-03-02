package be.kwakeroni.parameters.backend.inmemory.persistence;

import be.kwakeroni.evelyn.client.ClientTable;
import be.kwakeroni.parameters.backend.inmemory.api.EntryData;
import be.kwakeroni.parameters.backend.inmemory.api.GroupData;
import be.kwakeroni.parameters.backend.inmemory.api.InMemoryGroup;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.extension.mockito.MockitoExtension;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.AdditionalAnswers.answer;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PersistedGroupDataTest {

    @Mock
    private InMemoryGroup group;
    @Mock
    private ClientTable<EntryData> table;
    private PersistedGroupData groupData;

    @BeforeEach
    public void setupMocks() {
        when(group.validateNewEntry(any(), any())).then(returnsFirstArg());
    }

    @BeforeEach
    public void setupFields() {
        this.groupData = new PersistedGroupData(group, table);
    }

    @Test
    @DisplayName("Provides the backing group model")
    public void testGetGroup() {
        assertThat(groupData.getGroup()).isSameAs(group);
    }

    @Test
    @DisplayName("Provides entries from backing table")
    public void testGetEntries(@Mock Stream<EntryData> stream) {
        when(table.findAll()).thenReturn(stream);
        assertThat(groupData.getEntries()).isSameAs(stream);
    }

    @Nested
    @DisplayName("Inserts new entries")
    class AddEntryTest {
        @Test
        @DisplayName("With validation")
        public void testAddEntryValidation(@Mock EntryData entry) {
            groupData.addEntry(entry);

            verify(group).validateNewEntry(entry, groupData);
        }

        @Test
        @DisplayName("By appending an operation")
        public void testAddEntryAppending(@Mock EntryData entry) {

            Map<String, String> map = new LinkedHashMap<>();
            map.put("param1", "value-A");
            map.put("param2", "value-B");
            when(entry.asMap()).thenReturn(map);
            when(entry.getId()).thenReturn("myObjectId");

            groupData.addEntry(entry);

            verify(table).append("anonymous", "ADD", "myObjectId", "{\"param1\":\"value-A\",\"param2\":\"value-B\"}");
        }
    }

    @Nested
    @DisplayName("Modifies existing entries")
    class ModifyEntryTest {
        @Test
        @DisplayName("With validation")
        public void testModifyEntryValidation(@Mock EntryData entry, @Mock Consumer<EntryData> modifier, @Mock EntryData otherEntry1, @Mock EntryData otherEntry2) {
            when(table.findAll()).thenAnswer(inv -> Stream.of(otherEntry1, entry, otherEntry2));
            when(entry.getId()).thenReturn("myObjectId");
            when(otherEntry1.getId()).thenReturn("theirObjectId");
            when(otherEntry2.getId()).thenReturn("otherObjectId");

            groupData.modifyEntry(entry, modifier);

            ArgumentCaptor<GroupData> passedData = ArgumentCaptor.forClass(GroupData.class);
            verify(group).validateNewEntry(eq(entry), passedData.capture());

            assertThat(passedData.getValue().getEntries()).containsOnly(otherEntry1, otherEntry2);
        }

        @Test
        @DisplayName("By appending an operation")
        public void testAddEntryAppending(@Mock EntryData entry, @Mock Consumer<EntryData> modifier) {

            Map<String, String> map = new LinkedHashMap<>();
            map.put("param1", "value-A");
            map.put("param2", "value-B");
            when(entry.asMap()).thenReturn(map);
            when(entry.getId()).thenReturn("myObjectId");

            doAnswer(answer(e -> map.put("param2", "value-C"))).when(modifier).accept(entry);

            groupData.modifyEntry(entry, modifier);

            verify(modifier).accept(entry);
            verify(table).append("anonymous", "REPLACE", "myObjectId", "{\"param1\":\"value-A\",\"param2\":\"value-C\"}");
        }
    }

}