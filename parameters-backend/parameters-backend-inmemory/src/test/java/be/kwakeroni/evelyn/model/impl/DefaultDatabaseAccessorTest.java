package be.kwakeroni.evelyn.model.impl;

import be.kwakeroni.evelyn.model.Event;
import be.kwakeroni.evelyn.model.ParseException;
import be.kwakeroni.evelyn.model.RuntimeParseException;
import be.kwakeroni.evelyn.storage.Storage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.extension.mockito.MockitoExtension;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;

import java.nio.charset.Charset;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static be.kwakeroni.evelyn.test.Assertions.assertThat;
import static be.kwakeroni.evelyn.test.Assertions.assertThatParseExceptionThrownBy;
import static be.kwakeroni.evelyn.test.TestModel.event;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.entry;
import static org.mockito.AdditionalAnswers.answer;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DefaultDatabaseAccessorTest {

    private final LocalDateTime time0 = LocalDateTime.of(81, 12, 14, 16, 18);
    private final LocalDateTime time1 = LocalDateTime.now();

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
            DefaultDatabaseAccessor accessor = createFromStorageWith(
                    data(
                            event("anonymous", "a1", "INSERT", "ABC", time0),
                            event("myUser", "b2", "UPDATE", "DEF", time1)
                    ));

            List<Event> events = accessor.getData().collect(Collectors.toList());
            assertThat(events.get(0).getObjectId()).isEqualTo("a1");
            assertThat(events.get(0).getData()).isEqualTo("ABC");
            assertThat(events.get(0).getOperation()).isEqualTo("INSERT");
            assertThat(events.get(0).getUser()).isEqualTo("anonymous");
            assertThat(events.get(0).getTime()).isEqualTo(time0);
            assertThat(events.get(1).getObjectId()).isEqualTo("b2");
            assertThat(events.get(1).getData()).isEqualTo("DEF");
            assertThat(events.get(1).getOperation()).isEqualTo("UPDATE");
            assertThat(events.get(1).getUser()).isEqualTo("myUser");
            assertThat(events.get(1).getTime()).isEqualTo(time1);

        }

        @Test
        @DisplayName("with specific charset")
        public void testWithCharset(@Mock Storage storage, @Mock FileStructure fileStructure) throws Exception {
            DefaultDatabaseAccessor accessor = createFromStorageWith(storage, fileStructure, extraAttributes("charset", "ISO-8859-3"), noData());

            accessor.getData();

            verify(fileStructure).readData(storage, Charset.forName("ISO-8859-3"));
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

        @Test
        @DisplayName("in case of data mapping issues")
        public void testDataMappingException(@Mock Storage storage, @Mock FileStructure fileStructure, @Mock RecordStructure recordStructure) throws Exception {

            ParseException parseException = new ParseException("Test");
            when(recordStructure.toEvent(anyString())).thenAnswer(answer((String data) -> {
                if ("a2".equals(data)) {
                    throw parseException;
                } else {
                    return event(data);
                }
            }));

            when(fileStructure.readData(eq(storage), any())).then(answer(no -> new FileStructure.StreamData(Stream.of("a1", "a2", "a3"), 10)));
            when(storage.getReference()).thenReturn("SOURCE-FILE");

            DefaultDatabaseAccessor accessor = new DefaultDatabaseAccessor("", "", storage, requiredAttributes(), fileStructure, recordStructure);
            assertThatThrownBy(() -> accessor.getData().filter(Objects::isNull).findAny())
                    .isInstanceOf(RuntimeParseException.class)
                    .hasCause(parseException);

            assertThat(parseException)
                    .hasLine(12)
                    .hasSource("SOURCE-FILE");
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
    public void testCreate(@Mock Storage storage, @Mock FileStructure fileStructure, @Mock RecordStructure recordStructure) throws Exception {

        DefaultDatabaseAccessor accessor = new DefaultDatabaseAccessor("x.y", "myDb", storage, extraAttributes("custom", "myValue"), fileStructure, recordStructure);

        accessor.createDatabase();

        ArgumentCaptor<Map<String, String>> actualAtts = ArgumentCaptor.forClass(Map.class);

        verify(fileStructure).initializeStorage(eq(storage), actualAtts.capture(), eq("version"));

        assertThat(actualAtts.getValue()).containsOnly(
                entry("version", "x.y"),
                entry("name", "myDb"),
                entry("charset", Charset.defaultCharset().name()),
                entry("custom", "myValue")
        );
    }

    @Test
    @DisplayName("Appends data")
    public void testAppend(@Mock Storage storage, @Mock FileStructure fileStructure, @Mock RecordStructure recordStructure) throws Exception {

        when(recordStructure.toData(any())).thenReturn("myData");

        DefaultDatabaseAccessor accessor = new DefaultDatabaseAccessor("x.y", "myDb", storage, requiredAttributes(), fileStructure, recordStructure);

        Event event = event("myUser", "a1", "REPLACE", "GHI", LocalDateTime.now());
        accessor.append(event);

        verify(recordStructure).toData(event);
        verify(storage).append("myData");
    }

    private DefaultDatabaseAccessor createFromStorageWith(Map<String, String> attributes, Supplier<Stream<String>> data) throws ParseException {
        return createFromStorageWith(mock(Storage.class), mock(FileStructure.class), attributes, data);
    }

    private DefaultDatabaseAccessor createFromStorageWith(Storage storage, FileStructure fileStructure, Map<String, String> attributes, Supplier<Stream<String>> data) throws ParseException {

        when(fileStructure.readAttributes(storage)).thenReturn(attributes);
        when(fileStructure.readData(eq(storage), any())).then(answer(no -> new FileStructure.StreamData(data.get(), 0)));

        return DefaultDatabaseAccessor.createFrom(storage, fileStructure);
    }

    private DefaultDatabaseAccessor createFromStorageWith(Supplier<Stream<Event>> data) throws ParseException {
        Storage storage = mock(Storage.class);
        FileStructure fileStructure = mock(FileStructure.class);
        RecordStructure recordStructure = mock(RecordStructure.class);

        when(recordStructure.toEvent(anyString())).then(answer(line ->
                data.get()
                        .filter(event -> line.equals(event.getData()))
                        .findAny()
                        .orElseThrow(() -> new IllegalArgumentException("No matching test event found for '" + line + "'"))
        ));

        when(fileStructure.readData(eq(storage), any())).then(answer(no -> new FileStructure.StreamData(data.get().map(Event::getData), 0)));

        return new DefaultDatabaseAccessor("", "", storage, requiredAttributes(), fileStructure, recordStructure);
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

    private Supplier<Stream<Event>> data(Event... data) {
        return () -> Stream.of(data);
    }

}
