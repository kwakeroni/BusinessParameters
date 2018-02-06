package be.kwakeroni.evelyn.model.impl;

import be.kwakeroni.evelyn.model.Event;
import be.kwakeroni.evelyn.model.ParseException;
import be.kwakeroni.evelyn.model.test.TestModel;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class RecordStructureTest {

    @Nested
    @DisplayName("Parses event from string")
    class ParsingTest {


        @Test
        @DisplayName("without special characters")
        public void testSimple() throws Exception {
            Event event = toEvent(" 19811214161820|123-abc|myUser|INSERT|ABC-DATA");

            assertThat(event.getData()).isEqualTo("ABC-DATA");
            assertThat(event.getObjectId()).isEqualTo("123-abc");
            assertThat(event.getUser()).isEqualTo("myUser");
            assertThat(event.getTimestamp()).isEqualTo("19811214161820");
            assertThat(event.getTime()).isEqualTo(LocalDateTime.of(1981, 12, 14, 16, 18, 20));
            assertThat(event.getOperation()).isEqualTo("INSERT");
        }

        @Test
        @DisplayName("without escaping")
        public void testEscapeNone() throws Exception {
            Event event = toEvent(" 19811214161820|123-abc|myUser|INSERT|ABC-DA\\\\TA");

            assertThat(event.getData()).isEqualTo("ABC-DA\\\\TA");
        }

        @Test
        @DisplayName("with escaped pipe")
        public void testEscapePipe() throws Exception {
            Event event = toEvent("\\19811214161820|123-abc|myUser|INSERT|ABC\\|DATA");

            assertThat(event.getData()).isEqualTo("ABC|DATA");
        }


        @Test
        @DisplayName("with escaped backslash")
        public void testEscapeBackslash() throws Exception {
            Event event = toEvent("\\19811214161820|123-abc|myUser|INSERT|ABC-DA\\\\TA");

            assertThat(event.getData()).isEqualTo("ABC-DA\\TA");
        }

        @Test
        @DisplayName("with escaped line separator")
        public void testEscapeLineSeparator() throws Exception {
            Event event = toEvent("\\19811214161820|123-abc|myUser|INSERT|ABC\\nDATA");

            assertThat(event.getData()).isEqualTo("ABC" + System.lineSeparator() + "DATA");
        }


        @Test
        @DisplayName("with complex escaping")
        public void testEscapeComplex() throws Exception {
            Event event = toEvent("\\19811214161820|123-abc|myUser|INSERT|A\\nBC\\\\\\|DA\\\\nTA");

            assertThat(event.getData()).isEqualTo("A" + System.lineSeparator() + "BC\\|DA\\nTA");
        }

    }


    @Nested
    @DisplayName("Throws parsing errors")
    class ExceptionTest {

        @Test
        @DisplayName("when not enough columns are defined")
        public void testNotEnoughColumns() {
            assertThatThrownBy(() -> toEvent(" 811214161820|123-abc|myUser-INSERT|ABC-DATA"))
                    .isInstanceOf(ParseException.class);
        }

        @Test
        @DisplayName("when too many columns are defined")
        public void testTooManyColumns() {
            assertThatThrownBy(() -> toEvent(" 811214161820|123-abc|myUser|INSERT|ABC|DATA"))
                    .isInstanceOf(ParseException.class);
        }

    }

    @Nested
    @DisplayName("Writes event to string")
    class ToStringTest {

        @Test
        @DisplayName("without special characters")
        public void testSimple() {
            String data = toData(TestModel.event("myUser", "abc-789", "REPLACE", "{a=1}", "XYZ", LocalDateTime.of(1981, 12, 14, 16, 18, 20)));
            assertThat(data).isEqualTo(" 19811214161820|abc-789|myUser|REPLACE|{a=1}");
        }

        @Test
        @DisplayName("without escaped slash")
        public void testDontEscapeSlash() {
            String data = toData(TestModel.event("myUser", "abc-789", "REPLACE", "{a=\\}", "XYZ", LocalDateTime.of(1981, 12, 14, 16, 18, 20)));
            assertThat(data).isEqualTo(" 19811214161820|abc-789|myUser|REPLACE|{a=\\}");
        }


        @Test
        @DisplayName("with escaped pipe")
        public void testEscapePipe() {
            String data = toData(TestModel.event("myUser", "abc-789", "REPLACE", "{a=|}", "XYZ", LocalDateTime.of(1981, 12, 14, 16, 18, 20)));
            assertThat(data).isEqualTo("\\19811214161820|abc-789|myUser|REPLACE|{a=\\|}");
        }


        @Test
        @DisplayName("with escaped slash")
        public void testEscapeSlash() {
            String data = toData(TestModel.event("myUser", "abc-789", "REPLACE", "{a=\\|}", "XYZ", LocalDateTime.of(1981, 12, 14, 16, 18, 20)));
            assertThat(data).isEqualTo("\\19811214161820|abc-789|myUser|REPLACE|{a=\\\\\\|}");
        }

        @Test
        @DisplayName("with escaped newline (CR)")
        public void testEscapeCR() {
            String data = toData(TestModel.event("myUser", "abc-789", "REPLACE", "{\ra=1\r}", "XYZ", LocalDateTime.of(1981, 12, 14, 16, 18, 20)));
            assertThat(data).isEqualTo("\\19811214161820|abc-789|myUser|REPLACE|{\\na=1\\n}");
        }

        @Test
        @DisplayName("with escaped newline (LF)")
        public void testEscapeLF() {
            String data = toData(TestModel.event("myUser", "abc-789", "REPLACE", "{\na=1\n}", "XYZ", LocalDateTime.of(1981, 12, 14, 16, 18, 20)));
            assertThat(data).isEqualTo("\\19811214161820|abc-789|myUser|REPLACE|{\\na=1\\n}");
        }


        @Test
        @DisplayName("with escaped newline (CRLF)")
        public void testEscapeCRLF() {
            String data = toData(TestModel.event("myUser", "abc-789", "REPLACE", "{\r\na=1\r\n}", "XYZ", LocalDateTime.of(1981, 12, 14, 16, 18, 20)));
            assertThat(data).isEqualTo("\\19811214161820|abc-789|myUser|REPLACE|{\\na=1\\n}");
        }


    }

    @Nested
    @DisplayName("Is reversible")
    class ReversibleTest {
        @Test
        @DisplayName("without special characters")
        public void testSimple() throws Exception {
            assertReversible(" 19811214161820|abc-789|myUser|REPLACE|{a=1}");
        }


        @Test
        @DisplayName("with special characters")
        public void testEscapeComples() throws Exception {
            assertReversible("\\19811214161820|123-abc|myUser|INSERT|A\\nBC\\|DA\\\\nTA");
            assertReversible("\\19811214161820|123-abc|myUser|INSERT|A\\nBC\\|DA\\\\nTA");
        }


        private void assertReversible(String data) throws ParseException {
            assertThat(toData(toEvent(data))).isEqualTo(data);
        }
    }


    private RecordStructure getInstance() {
        return RecordStructure.getInstance();
    }

    private Event toEvent(String data) throws ParseException {
        return getInstance().toEvent(data);
    }

    private String toData(Event event) {
        return getInstance().toData(event);
    }

}
