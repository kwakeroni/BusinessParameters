package be.kwakeroni.parameters.backend.inmemory.persistence;

import be.kwakeroni.evelyn.test.TestModel;
import be.kwakeroni.parameters.backend.inmemory.api.EntryData;
import be.kwakeroni.parameters.backend.inmemory.support.DefaultEntryData;
import be.kwakeroni.parameters.backend.inmemory.support.JsonEntryData;
import be.kwakeroni.test.factory.TestMap;
import org.json.JSONObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.extension.mockito.MockitoExtension;
import org.mockito.Mock;

import java.time.LocalDateTime;
import java.util.Collections;

import static be.kwakeroni.parameters.backend.inmemory.persistence.GroupTableOperation.ADD;
import static be.kwakeroni.parameters.backend.inmemory.persistence.GroupTableOperation.REPLACE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GroupTableOperationTest {

    @Nested
    @DisplayName("Supports ADD operation")
    class AddTest {

        @DisplayName("Creates a new entry")
        @Test
        void testCreateNewEntry() {
            EntryData entry = ADD.operate(null, TestModel.event("myUser", "myId", "ADD", "{\"param\":\"value\"}", LocalDateTime.now()));
            assertThat(entry).isNotNull();
            assertThat(entry.getId()).isEqualTo("myId");
            assertThat(entry.getValue("param")).isEqualTo("value");
        }

        @DisplayName("Fails if an entry already exists")
        @Test
        void testThrowOnExistingEntry(@Mock EntryData entryData) {

            when(entryData.getId()).thenReturn("myEntryId");

            assertThatThrownBy(() ->
                    ADD.operate(entryData, TestModel.event("myUser", "myId", "ADD", "myData", LocalDateTime.now())))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("myEntryId");
        }

        @DisplayName("Fails if the event provides no id")
        @Test
        void testThrowOnNullId() {
            assertThatThrownBy(() ->
                    ADD.operate(null, TestModel.event("myUser", null, "ADD", "myData", LocalDateTime.now())))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }

    @Nested
    @DisplayName("Supports REPLACE operation")
    class ReplaceTest {
        @DisplayName("Replaces an entry")
        @Test
        void testReplaceEntry(@Mock EntryData original) {
            when(original.getId()).thenReturn("myId");
            when(original.asMap()).thenReturn(Collections.singletonMap("param", "original value"));

            EntryData entry = REPLACE.operate(original, TestModel.event("myUser", "myId", "REPLACE", "{\"param\":\"value\"}", LocalDateTime.now()));
            assertThat(entry).isNotNull();
            assertThat(entry).isNotEqualTo(original);
            assertThat(entry.getId()).isEqualTo(original.getId());
            assertThat(entry.getValue("param")).isEqualTo("value");
        }

        @DisplayName("Fails when replacing with a different id")
        @Test
        void testThrowOnDifferentId(@Mock EntryData original) {
            when(original.getId()).thenReturn("originalId");
            assertThatThrownBy(() -> REPLACE.operate(original, TestModel.event("myUser", "myId", "REPLACE", "{\"param\":\"value\"}", LocalDateTime.now())))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("originalId")
                    .hasMessageContaining("myId");
        }

        @DisplayName("Fails if the entry does not exist")
        @Test
        void testThrowOnNonExistingEntry() {
            assertThatThrownBy(() -> REPLACE.operate(null, TestModel.event("myUser", "myId", "REPLACE", "{\"param\":\"value\"}", LocalDateTime.now())))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("myId");
        }
    }

    @Nested
    @DisplayName("Transforms from and to String")
    class StringTest {
        @DisplayName("Creates an entry from a JSON String")
        @Test
        void testFromString() {
            EntryData entry = GroupTableOperation.fromString("myId", "{\"param\":\"value\"}");
            assertThat(entry.getId()).isEqualTo("myId");
            assertThat(entry.getValue("param")).isEqualTo("value");
        }

        @DisplayName("Transforms a JSON Entry to a JSON String")
        @Test
        void testJsonEntryToString() {
            JsonEntryData entry = JsonEntryData.of("myId", new JSONObject(TestMap.of("param", "value")));
            assertThat(GroupTableOperation.toString(entry)).isEqualTo("{\"param\":\"value\"}");
        }

        @DisplayName("Transforms any Entry to a JSON String")
        @Test
        void testAnyEntryToString() {
            DefaultEntryData entry = DefaultEntryData.of("myId", TestMap.of("param", "value"));
            assertThat(GroupTableOperation.toString(entry)).isEqualTo("{\"param\":\"value\"}");
        }


    }

}