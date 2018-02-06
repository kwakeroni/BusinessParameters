package be.kwakeroni.evelyn.storage.impl;

import be.kwakeroni.evelyn.model.test.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.extension.mockito.MockitoExtension;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.Iterator;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class HeaderStructureTest {

    private static final String VERSION = "2.c";
    private static final String[] DATA = new String[]{
            "!evelyn-db",
            "!version=" + VERSION,
            "!name=myDb",
            "!data",
            "ABC",
            "DEF"
    };

    @Nested
    @DisplayName("With Stream input")
    class StreamInputTest {

        @Test
        @DisplayName("Parses version from header")
        public void testReadVersion() throws Exception {
            String version = HeaderStructure.readVersion(() -> Arrays.stream(DATA));
            assertThat(version).isEqualTo(VERSION);
        }

        @Test
        @DisplayName("Throws exception for empty header")
        public void testEmpty() throws Exception {

            Assertions.assertThatParseExceptionThrownBy(
                    () -> HeaderStructure.readVersion(() -> Stream.of()))
                    .hasLine(1)
                    .hasPosition(1);
        }

        @Test
        @DisplayName("Throws exception for incomplete header")
        public void testIncomplete() throws Exception {

            Assertions.assertThatParseExceptionThrownBy(
                    () -> HeaderStructure.readVersion(() -> Stream.of("!evelyn-db")))
                    .hasLine(2)
                    .hasPosition(1);
        }


        @Test
        @DisplayName("Throws exception for incorrect header")
        public void testIncorrectHeader() throws Exception {

            Assertions.assertThatParseExceptionThrownBy(
                    () -> HeaderStructure.readVersion(() -> Stream.of("xyz", "abc")))
                    .hasLine(1);
        }

        @Test
        @DisplayName("Throws exception for incorrect version specification")
        public void testIncorrectVersionSpec() throws Exception {

            Assertions.assertThatParseExceptionThrownBy(
                    () -> HeaderStructure.readVersion(() -> Stream.of("!evelyn-db", "!version =")))
                    .hasLine(2);
        }
    }

    @Nested
    @DisplayName("With Iterator input")
    class IteratorInputTest {
        @Test
        @DisplayName("Skips the first header line")
        public void testSkipFirstLine(@Mock Iterator<String> iterator) throws Exception {
            when(iterator.hasNext()).thenReturn(true);
            when(iterator.next()).thenReturn("!evelyn-db");

            HeaderStructure.readToAttributes(iterator);

            verify(iterator).next();
        }

        @Test
        @DisplayName("Returns number of lines skipped")
        public void testReturnsNumberOfLines(@Mock Iterator<String> iterator) throws Exception {
            when(iterator.hasNext()).thenReturn(true);
            when(iterator.next()).thenReturn("!evelyn-db");

            int lines = HeaderStructure.readToAttributes(iterator);

            assertThat(lines).isEqualTo(1);
        }

        @Test
        @DisplayName("Throws exception for empty header")
        public void testEmpty(@Mock Iterator<String> iterator) throws Exception {
            when(iterator.hasNext()).thenReturn(false);

            Assertions.assertThatParseExceptionThrownBy(
                    () -> HeaderStructure.readToAttributes(iterator))
                    .hasLine(1)
                    .hasPosition(1);
        }

        @Test
        @DisplayName("Throws exception for incorrect header")
        public void testIncorrectHeader(@Mock Iterator<String> iterator) throws Exception {
            when(iterator.hasNext()).thenReturn(true);
            when(iterator.next()).thenReturn("!evelyn-somethingelse");

            Assertions.assertThatParseExceptionThrownBy(
                    () -> HeaderStructure.readToAttributes(iterator))
                    .hasLine(1);
        }

    }

    @Nested
    @DisplayName("With an output")
    public class OutputTest {
        @Test
        @DisplayName("Writes the header")
        public void testWriteHeader(@Mock Consumer<String> sink) {
            HeaderStructure.writeHeader("v.1", sink);

            InOrder inOrder = Mockito.inOrder(sink);
            inOrder.verify(sink).accept("!evelyn-db");
            inOrder.verify(sink).accept("!version=v.1");
        }
    }

}
