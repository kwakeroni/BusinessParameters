package be.kwakeroni.evelyn.model;

import be.kwakeroni.evelyn.model.impl.Version;
import be.kwakeroni.evelyn.storage.Storage;
import be.kwakeroni.evelyn.storage.StorageProvider;
import be.kwakeroni.evelyn.test.TestModel;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.extension.mockito.MockitoExtension;
import org.mockito.Mock;

import java.util.function.Function;
import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DefaultDatabaseProviderTest {

    private SpyDefaultDatabaseProvider spyProvider = new SpyDefaultDatabaseProvider();
    private DefaultDatabaseProvider defaultProvider = (DefaultDatabaseProvider) DefaultDatabaseProvider.getInstance();
    @Mock
    private Function<String, DatabaseProvider> getProviderByVersion;
    @Mock
    private Supplier<DatabaseProvider> getDefaultProvider;

    @Nested
    @DisplayName("Reads a database from storage")
    class ReadTest {

        @Test
        @DisplayName("Creates a Database Accessor")
        void testRead(@Mock DatabaseProvider dbProvider, @Mock DatabaseAccessor accessor) throws Exception {
            Storage storage = TestModel.asStorage("0.1", "myDb", new Event[0]);
            when(storage.readVersion()).thenReturn("x.y");
            when(getProviderByVersion.apply(anyString())).thenReturn(dbProvider);
            when(dbProvider.read(any(Storage.class))).thenReturn(accessor);

            DatabaseAccessor actual = spyProvider.read(storage);
            assertThat(actual).isSameAs(accessor);

            verify(storage).readVersion();
            verify(getProviderByVersion).apply("x.y");
            verify(dbProvider).read(storage);
        }

        @Test
        @DisplayName("Delegates to the specified Version as database provider")
        void testGetProviderByVersion() {
            DatabaseProvider provider = defaultProvider.getProviderByVersion("0.1");
            assertThat(provider).isSameAs(Version.V0_1);
        }

        @Test
        @DisplayName("Throws DatabaseException in case of parsing errors")
        void testThrowDatabaseException() throws Exception {
            Storage storage = TestModel.asStorage("0.1", "myDb", new Event[0]);
            ParseException parseException = new ParseException("Test");
            when(storage.readVersion()).thenThrow(parseException);

            assertThatThrownBy(() -> defaultProvider.read(storage))
                    .isInstanceOf(DatabaseException.class)
                    .hasCause(parseException);
        }
    }

    @Nested
    @DisplayName("Creates a database with provided storage")
    class CreateTest {
        @Test
        @DisplayName("Delegates to the default database provider")
        void testCreate(@Mock StorageProvider storageProvider, @Mock DatabaseProvider defaultProvider, @Mock DatabaseAccessor accessor) throws Exception {
            when(getDefaultProvider.get()).thenReturn(defaultProvider);
            when(defaultProvider.create(any(StorageProvider.class), anyString())).thenReturn(accessor);


            DatabaseAccessor actual = spyProvider.create(storageProvider, "myDb");

            assertThat(actual).isSameAs(accessor);
            verify(getDefaultProvider).get();
            verify(defaultProvider).create(storageProvider, "myDb");
        }

        @Test
        @DisplayName("Uses the latest version as default database provider")
        void testGetDefaultProvider() {
            DatabaseProvider actual = defaultProvider.getDefaultProvider();
            assertThat(actual).isSameAs(Version.LATEST);
        }
    }

    private class SpyDefaultDatabaseProvider extends DefaultDatabaseProvider {
        @Override
        DatabaseProvider getProviderByVersion(String version) {
            return getProviderByVersion.apply(version);
        }

        @Override
        DatabaseProvider getDefaultProvider() {
            return getDefaultProvider.get();
        }
    }
}