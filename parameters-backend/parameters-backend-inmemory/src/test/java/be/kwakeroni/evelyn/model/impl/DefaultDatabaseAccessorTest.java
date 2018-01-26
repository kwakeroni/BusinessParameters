package be.kwakeroni.evelyn.model.impl;

import be.kwakeroni.evelyn.model.Event;
import be.kwakeroni.evelyn.model.ParseException;
import be.kwakeroni.evelyn.storage.Storage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.extension.mockito.MockitoExtension;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static be.kwakeroni.evelyn.model.test.Assertions.assertThat;
import static be.kwakeroni.evelyn.model.test.Assertions.assertThatParseExceptionThrownBy;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.entry;
import static org.mockito.AdditionalAnswers.answer;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DefaultDatabaseAccessorTest {

    @Nested
    @DisplayName("Constructs instances")
    class ReadTest {

        @Test
        @DisplayName("using a constructor")
        public void testConstructor(@Mock Storage storage) {
            DefaultDatabaseAccessor accessor = new DefaultDatabaseAccessor("x.y", "myDb", storage);

            assertThat(accessor.getSpecVersion()).isEqualTo("x.y");
            assertThat(accessor.getDatabaseName()).isEqualTo("myDb");
        }

        @Test
        @DisplayName("from storage")
        public void testCreateFrom() throws Exception {
            DefaultDatabaseAccessor accessor = createFromStorageWith(
                    attributes(
                            "version", "x.y",
                            "name", "myDb"
                    ), noData());

            assertThat(accessor).isNotNull();
            assertThat(accessor.getSpecVersion()).isEqualTo("x.y");
            assertThat(accessor.getDatabaseName()).isEqualTo("myDb");
        }

        @Test
        @DisplayName("with custom attributes")
        public void testCustomAttributes() throws Exception {

            DefaultDatabaseAccessor accessor = createFromStorageWith(
                    extraAttributes("custom", "customValue"),
                    noData());

            assertThat(accessor.getAttribute("custom")).isEqualTo("customValue");
        }

        @Test
        @DisplayName("without data")
        public void testNoData() throws Exception {
            DefaultDatabaseAccessor accessor = createFromStorageWith(requiredAttributes(), noData());

            assertThat(accessor.getData().findAny()).isNotPresent();
        }

        @Test
        @DisplayName("with data")
        public void testWithData() throws Exception {
            DefaultDatabaseAccessor accessor = createFromStorageWith(requiredAttributes(),
                    data(
                            "ABC",
                            "DEF"
                    ));

            assertThat(accessor.getData().map(Event::getData)).containsExactly("ABC", "DEF");
        }
    }

    @Nested
    @DisplayName("Throws parsing errors")
    class ParseErrorTest {

        @Test
        @DisplayName("for missing required attribute")
        public void testMissingRequiredAttribute() {
            assertThatParseExceptionThrownBy(() ->
                    createFromStorageWith(attributes("version", "x.y"), noData())
            );
        }
    }

    @Test
    @DisplayName("Closes a data stream that could not be returned")
    public void testClosesDataStream(@Mock Stream<String> data) throws Exception {

        when(data.map(any())).thenThrow(new UnsupportedOperationException("Test"));

        DefaultDatabaseAccessor accessor = createFromStorageWith(requiredAttributes(), () -> data);

        assertThatThrownBy(accessor::getData)
                .isInstanceOf(UnsupportedOperationException.class);

        verify(data).close();
    }

    @Test
    @DisplayName("Creates an empty database")
    public void testCreate(@Mock Storage storage, @Mock FileStructure fileStructure) {

        DefaultDatabaseAccessor accessor = new DefaultDatabaseAccessor("x.y", "myDb", storage, extraAttributes("custom", "myValue"), fileStructure);

        accessor.createDatabase();

        ArgumentCaptor<Map<String, String>> actualAtts = ArgumentCaptor.forClass(Map.class);

        verify(fileStructure).initializeStorage(eq(storage), actualAtts.capture(), eq("version"));

        assertThat(actualAtts.getValue()).containsOnly(
                entry("version", "x.y"),
                entry("name", "myDb"),
                entry("custom", "myValue")
        );
    }

    private DefaultDatabaseAccessor createFromStorageWith(Map<String, String> attributes, Supplier<Stream<String>> data) throws ParseException {
        Storage storage = mock(Storage.class);
        FileStructure fileStructure = mock(FileStructure.class);

        when(fileStructure.readAttributes(storage)).thenReturn(attributes);
        when(fileStructure.readData(storage)).then(answer(no -> data.get()));

        return DefaultDatabaseAccessor.createFrom(storage, fileStructure);
    }

    private Map<String, String> requiredAttributes() {
        return extraAttributes();
    }

    private Map<String, String> extraAttributes(String... keyValuePairs) {
        List<String> attributes = new ArrayList<>();
        attributes.addAll(Arrays.asList("version", "0.0", "name", "myName"));
        attributes.addAll(Arrays.asList(keyValuePairs));
        return attributes(attributes);
    }

    private Map<String, String> attributes(String... keyValuePairs) {
        return attributes(Arrays.asList(keyValuePairs));
    }

    private Map<String, String> attributes(Iterable<String> keyValuePairs) {
        Map<String, String> map = new HashMap<>();
        Iterator<String> iter = keyValuePairs.iterator();
        while (iter.hasNext()) {
            map.put(iter.next(), iter.next());
        }
        return Collections.unmodifiableMap(map);
    }

    private Supplier<Stream<String>> noData() {
        return Stream::empty;
    }

    private Supplier<Stream<String>> data(String... data) {
        return () -> Stream.of(data);
    }

}
