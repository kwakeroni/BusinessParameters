package be.kwakeroni.evelyn.model.versions;

import be.kwakeroni.evelyn.storage.Storage;
import org.assertj.core.api.ThrowableAssert;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.extension.mockito.MockitoExtension;
import org.mockito.Mock;
import org.mockito.stubbing.Stubber;
import org.mockito.stubbing.VoidAnswer1;

import java.util.Arrays;
import java.util.Collection;

import static be.kwakeroni.evelyn.model.parser.ParseExceptionAssert.assertThatParseExceptionThrownBy;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.AdditionalAnswers.answerVoid;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DatabaseAccessorSupportTest {

    @Nested
    @DisplayName("Constructs instances")
    class ReadTest {

        @Test
        @DisplayName("using a constructor")
        public void testConstructor(@Mock Storage storage) {
            DatabaseAccessorSupport accessor = new DatabaseAccessorSupport("x.y", "myDb", storage);

            assertThat(accessor.getSpecVersion()).isEqualTo("x.y");
            assertThat(accessor.getDatabaseName()).isEqualTo("myDb");
        }

        @Test
        @DisplayName("from storage")
        public void testCreateFrom(@Mock Storage storage) throws Exception {
            DatabaseAccessorSupport accessor = DatabaseAccessorSupport.createFrom(asStorage(
                    "!evelyn-db",
                    "!version=x.y",
                    "!name=myDb"
            ));

            assertThat(accessor).isNotNull();
            assertThat(accessor.getSpecVersion()).isEqualTo("x.y");
            assertThat(accessor.getDatabaseName()).isEqualTo("myDb");
        }
    }

    @Nested
    @DisplayName("Throws parsing errors")
    class ParseErrorTest {
        @Test
        @DisplayName("for empty file")
        public void testEmptyFileError() throws Exception {
            assertThatParseExceptionThrownBy(parsing())
                    .hasLine(1)
                    .hasPosition(1);
        }

        @Test
        @DisplayName("for unrecognized header")
        public void testWrongHeader() throws Exception {
            assertThatParseExceptionThrownBy(
                    parsing("!version=x.y"
                    ))
                    .hasLine(1);
        }


        @Test
        @DisplayName("for unrecognized line")
        public void testWrongAttributeLine() throws Exception {
            assertThatParseExceptionThrownBy(
                    parsing("!evelyn-db",
                            "!version=x.y",
                            "@name=myDb"
                    ))
                    .hasLine(3)
                    .hasPosition(1);
        }

        @Test
        @DisplayName("for missing attribute value")
        public void testMissingAttributeValue() throws Exception {
            assertThatParseExceptionThrownBy(
                    parsing("!evelyn-db",
                            "!version:x.y",
                            "!name=myDb"
                    ))
                    .hasLine(2)
                    .hasPosition(12);
        }

        @Test
        @DisplayName("for '=' in attribute value")
        public void testMultipleEqualsSigns() throws Exception {
            assertThatParseExceptionThrownBy(
                    parsing("!evelyn-db",
                            "!version=x.y",
                            "!name=my=Db"
                    ))
                    .hasLine(3)
                    .hasPosition(9);
        }

        @Test
        @DisplayName("for missing required attribute")
        public void testMissingRequiredAttribute() throws Exception {
            assertThatParseExceptionThrownBy(
                    parsing("!evelyn-db",
                            "!version=x.y"
                    ));
        }

        @Test
        @DisplayName("for duplicate attribute")
        public void testDuplicateAttribute() throws Exception {
            assertThatParseExceptionThrownBy(
                    parsing("!evelyn-db",
                            "!version=x.y",
                            "!att=value",
                            "!version=x.y"
                    ))
                    .hasLine(4);
        }


        private ThrowableAssert.ThrowingCallable parsing(String... contents) {
            return () -> {
                DatabaseAccessorSupport.createFrom(asStorage(contents));
            };
        }
    }

    @Test
    @DisplayName("creates an empty database")
    public void testCreate(@Mock Storage storage) {
        DatabaseAccessorSupport accessor = new DatabaseAccessorSupport("x.y", "myDb", storage);

        accessor.createDatabase();

        verify(storage).writeHeader("x.y");
    }

    private static <T> Stubber collectInto(Collection<T> collection) {
        return doAnswer(answerVoid((VoidAnswer1<T>) collection::add));
    }

    private static Storage asStorage(String... contents) {
        Storage storage = mock(Storage.class);
        when(storage.read()).thenAnswer(no -> Arrays.stream(contents));
        return storage;
    }
}
