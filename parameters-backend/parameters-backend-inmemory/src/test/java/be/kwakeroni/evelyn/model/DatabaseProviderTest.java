package be.kwakeroni.evelyn.model;

import be.kwakeroni.evelyn.storage.Storage;
import be.kwakeroni.evelyn.storage.StorageProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.extension.mockito.MockitoExtension;
import org.mockito.Answers;
import org.mockito.Mock;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DatabaseProviderTest {

    @Mock(answer = Answers.CALLS_REAL_METHODS)
    private DatabaseProvider provider;

    @DisplayName("Creates a database if storage does not yet exist")
    @Test
    void testReadCreateCreates(@Mock StorageProvider storageProvider, @Mock DatabaseAccessor accessor) throws Exception {
        when(provider.create(any(), anyString())).thenReturn(accessor);

        DatabaseAccessor actual = provider.readCreate(storageProvider, "myDb");
        assertThat(actual).isSameAs(accessor);

        verify(provider).create(storageProvider, "myDb");
    }

    @DisplayName("Reads a database if storage already exists")
    @Test
    void testReadCreateReads(@Mock StorageProvider storageProvider, @Mock Storage storage, @Mock DatabaseAccessor accessor) throws Exception {
        when(provider.read(any())).thenReturn(accessor);
        when(storageProvider.read(anyString())).thenReturn(storage);
        when(storageProvider.exists(anyString())).thenReturn(true);

        DatabaseAccessor actual = provider.readCreate(storageProvider, "myDb");
        assertThat(actual).isSameAs(accessor);

        verify(storageProvider).exists("myDb");
        verify(storageProvider).read("myDb");
        verify(provider).read(storage);
    }
}