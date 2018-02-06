package be.kwakeroni.evelyn.model;


import be.kwakeroni.evelyn.storage.Storage;
import be.kwakeroni.evelyn.storage.StorageExistsException;
import be.kwakeroni.evelyn.storage.StorageProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.extension.mockito.MockitoExtension;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.converter.ArgumentConversionException;
import org.junit.jupiter.params.converter.ArgumentConverter;
import org.junit.jupiter.params.converter.ConvertWith;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@DisplayName("Database version")
@ExtendWith(MockitoExtension.class)
public class VersionTest {
    @ValueSource(strings = {
            "0.1"
    })
    @ParameterizedTest(name = "version {0}")
    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    private static @interface TestAllVersions {
    }

    @ConvertWith(VersionArgumentConverter.class)
    @Target(ElementType.PARAMETER)
    @Retention(RetentionPolicy.RUNTIME)
    private static @interface As {
    }

    @TestAllVersions
    @DisplayName("supports versions")
    public void testVersionSupported(String versionNumber) {
        Version version = Version.byNumber(versionNumber);
        assertThat(version).isNotNull();
    }

    @Test
    @DisplayName("throws exception for unsupported versions")
    public void testUnsupported() {
        assertThatThrownBy(() -> Version.byNumber("unsupported"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("unsupported");
    }

    @TestAllVersions
    @DisplayName("creates a database")
    public void testCreateDatabase(
            @As Version version,
            @Mock StorageProvider storageProvider,
            @Mock Storage storage
    ) throws Exception {
        when(storageProvider.create(anyString())).thenReturn(storage);

        DatabaseAccessor accessor = version.create(storageProvider, "myDatabase");

        assertThat(accessor).isNotNull();
        assertThat(accessor.getSpecVersion()).isEqualTo(version.getVersionNumber());
        assertThat(accessor.getDatabaseName()).isEqualTo("myDatabase");

        verify(storageProvider).create("myDatabase");
    }

    @TestAllVersions
    @DisplayName("handles database creation exceptions")
    public void testCreateDatabaseException(
            @As Version version,
            @Mock StorageProvider storageProvider
    ) throws Exception {
        when(storageProvider.create(anyString())).thenThrow(new StorageExistsException("Storage for 'myDatabase' already exists"));

        assertThatThrownBy(() -> version.create(storageProvider, "myDatabase"))
                .isInstanceOf(DatabaseException.class)
                .hasMessageContaining("myDatabase");

        verify(storageProvider).create("myDatabase");
    }

    @TestAllVersions
    @DisplayName("reads a database")
    public void testReadDatabase(
            @As Version version,
            @Mock Storage storage
    ) throws Exception {
        when(storage.read(any())).thenAnswer(no -> Stream.of(
                "!evelyn-db",
                "!version=x.y",
                "!name=myDatabase"
        ));

        DatabaseAccessor accessor = version.read(storage);

        assertThat(accessor).isNotNull();
        assertThat(accessor.getSpecVersion()).isEqualTo("x.y");
        assertThat(accessor.getDatabaseName()).isEqualTo("myDatabase");
    }

    @TestAllVersions
    @DisplayName("handles database read exceptions")
    public void testReadDatabaseException(
            @As Version version,
            @Mock Storage storage
    ) throws Exception {
        when(storage.read(any())).thenAnswer(no -> Stream.of());

        assertThatThrownBy(() -> version.read(storage))
                .isInstanceOf(DatabaseException.class)
                .hasCauseInstanceOf(ParseException.class);

    }


    private static class VersionArgumentConverter implements ArgumentConverter {
        @Override
        public Object convert(Object versionNumber, ParameterContext parameterContext) throws ArgumentConversionException {
            try {
                return Version.byNumber((String) versionNumber);
            } catch (ClassCastException exc) {
                throw new ArgumentConversionException("Version number not String: " + exc.getMessage());
            } catch (Exception exc) {
                throw new ArgumentConversionException(exc.getMessage());
            }
        }
    }
}

