package be.kwakeroni.evelyn.model.impl;

import be.kwakeroni.evelyn.model.ParseException;
import be.kwakeroni.evelyn.model.test.SilentCloseable;
import be.kwakeroni.evelyn.storage.Storage;
import org.assertj.core.api.ThrowableAssert;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.extension.mockito.MockitoExtension;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.nio.charset.Charset;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Stream;

import static be.kwakeroni.evelyn.model.test.Assertions.*;
import static be.kwakeroni.evelyn.model.test.TestModel.STORAGE_SOURCE;
import static be.kwakeroni.evelyn.model.test.TestModel.asStorage;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class FileStructureTest {

    @Test
    @DisplayName("Reads common attributes")
    public void testCreateFrom() throws Exception {
        Map<String, String> attributes = readAttributesFrom(
                "!evelyn-db",
                "!version=x.y",
                "!name=myDb"
        );

        assertThat(attributes).containsOnly(
                entry("version", "x.y"),
                entry("name", "myDb"));
    }

    @Test
    @DisplayName("Reads custom attributes")
    public void testCustomAttributes() throws Exception {
        Map<String, String> attributes = readAttributesFrom(
                "!evelyn-db",
                "!version=x.y",
                "!name=myDb",
                "!custom=customValue"
        );

        assertThat(attributes.get("custom")).isEqualTo("customValue");
    }

    @Test
    @DisplayName("Reads no data if there is none")
    public void testNoData() throws Exception {
        FileStructure.StreamData streamData = readDataFrom(
                "!evelyn-db",
                "!version=x.y",
                "!name=myDb",
                "!data"
        );

        assertThat(streamData.dataStream).isEmpty();
        assertThat(streamData.linePointer).isEqualTo(4);
    }

    @Test
    @DisplayName("Reads data")
    public void testWithData() throws Exception {
        FileStructure.StreamData streamData = readDataFrom(
                "!evelyn-db",
                "!version=x.y",
                "!name=myDb",
                "!data",
                "ABC",
                "DEF"
        );

        assertThat(streamData.dataStream).containsExactly("ABC", "DEF");
        assertThat(streamData.linePointer).isEqualTo(4);
    }

    @Nested
    @DisplayName("Throws parsing errors")
    class ParseErrorTest {
        @Test
        @DisplayName("for empty file")
        public void testEmptyFileError() {
            assertThatParseExceptionThrownBy(reading(/* no data */))
                    .hasLine(1)
                    .hasPosition(1)
                    .hasSource(STORAGE_SOURCE);
        }

        @Test
        @DisplayName("for unrecognized header")
        public void testWrongHeader() {
            assertThatParseExceptionThrownBy(
                    reading("!version=x.y"
                    ))
                    .hasLine(1)
                    .hasSource(STORAGE_SOURCE);
        }


        @Test
        @DisplayName("for unrecognized line")
        public void testWrongAttributeLine() {
            assertThatParseExceptionThrownBy(
                    reading("!evelyn-db",
                            "!version=x.y",
                            "@name=myDb"
                    ))
                    .hasLine(3)
                    .hasPosition(1)
                    .hasSource(STORAGE_SOURCE);
        }

        @Test
        @DisplayName("for missing attribute value")
        public void testMissingAttributeValue() {
            assertThatParseExceptionThrownBy(
                    reading("!evelyn-db",
                            "!version:x.y",
                            "!name=myDb"
                    ))
                    .hasLine(2)
                    .hasPosition(12)
                    .hasSource(STORAGE_SOURCE);
        }

        @Test
        @DisplayName("for '=' in attribute value")
        public void testMultipleEqualsSigns() {
            assertThatParseExceptionThrownBy(
                    reading("!evelyn-db",
                            "!version=x.y",
                            "!name=my=Db"
                    ))
                    .hasLine(3)
                    .hasPosition(9)
                    .hasSource(STORAGE_SOURCE);
        }

        @Test
        @DisplayName("for duplicate attribute")
        public void testDuplicateAttribute() {
            assertThatParseExceptionThrownBy(
                    reading("!evelyn-db",
                            "!version=x.y",
                            "!att=value",
                            "!version=x.y"
                    ))
                    .hasLine(4)
                    .hasSource(STORAGE_SOURCE);
        }

    }

    @Nested
    @DisplayName("Closes backing resource")
    class CloseResourceTest {
        @Test
        @DisplayName("after retrieving attributes")
        public void closesAfterAttributes(@Mock SilentCloseable resource) throws Exception {
            Map<String, String> attributes = readAttributesFrom(resource,
                    "!evelyn-db",
                    "!version=x.y",
                    "!name=myDb",
                    "!data",
                    "ABC",
                    "DEF");

            verify(resource).close();

        }

        @Test
        @DisplayName("after closing data stream")
        public void closesAfterDataStream(@Mock SilentCloseable resource) throws Exception {
            FileStructure.StreamData streamData = readDataFrom(resource,
                    "!evelyn-db",
                    "!version=x.y",
                    "!name=myDb",
                    "!data",
                    "ABC",
                    "DEF");

            try (Stream<String> toClose = streamData.dataStream) {
                verify(resource, never()).close();
            }

            verify(resource).close();
        }
    }

    @Nested
    @DisplayName("writes to storage")
    class WriteTest {
        @Test
        @DisplayName("initialization of headers")
        public void initializesStorage(@Mock Storage storage) throws Exception {
            Map<String, String> attributes = new LinkedHashMap<>();
            attributes.put("version", "x.y");
            attributes.put("name", "myDb");
            attributes.put("custom", "myValue");

            getInstance().initializeStorage(storage, attributes, "version");

            InOrder inOrder = Mockito.inOrder(storage);
            inOrder.verify(storage).writeHeader("x.y");
            inOrder.verify(storage).append("!name=myDb");
            inOrder.verify(storage).append("!custom=myValue");
            inOrder.verify(storage).append("!data");
        }
    }


    private FileStructure getInstance() {
        return FileStructure.getInstance();
    }

    private Map<String, String> readAttributesFrom(String... contents) throws ParseException {
        return getInstance().readAttributes(asStorage(contents));
    }

    private Map<String, String> readAttributesFrom(SilentCloseable resource, String... contents) throws ParseException {
        return getInstance().readAttributes(asStorage(resource, contents));
    }

    private FileStructure.StreamData readDataFrom(String... contents) throws ParseException {
        return getInstance().readData(asStorage(contents), Charset.defaultCharset());
    }

    private FileStructure.StreamData readDataFrom(SilentCloseable resource, String... contents) throws ParseException {
        return getInstance().readData(asStorage(resource, contents), Charset.defaultCharset());
    }

    private ThrowableAssert.ThrowingCallable reading(String... contents) {
        return () -> getInstance().readAttributes(asStorage(contents));
    }

}
