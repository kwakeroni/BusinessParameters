package be.kwakeroni.evelyn.storage.impl;

import be.kwakeroni.evelyn.storage.Storage;
import be.kwakeroni.evelyn.storage.StorageExistsException;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@RunWith(MockitoJUnitRunner.class)
public class FileStorageProviderTest {

    @ClassRule
    public static TemporaryFolder folder = new TemporaryFolder();

    private FileStorageProvider provider = new FileStorageProvider(folder.getRoot().toPath());

    @Test
    public void testFileNotExists() {
        assertThat(provider.exists("notexists")).isFalse();
    }

    @Test
    public void testFileExists() throws Exception {
        folder.newFile("exists.edb");
        assertThat(provider.exists("exists")).isTrue();
    }

    @Test
    public void testReadExists() throws Exception {
        folder.newFile("readexists.edb");
        Path expected = folder.getRoot().toPath().resolve("readexists.edb");

        Storage storage = provider.read("readexists");

        assertThat(storage).isInstanceOf(FileStorage.class);
        assertThat(((FileStorage) storage).getStorageLocation()).isEqualTo(expected);
    }

    @Test
    public void testReadNotExists() {
        assertThatThrownBy(() -> provider.read("readnotexists"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("readnotexists");
    }

    @Test
    public void testCreateNotExists() throws Exception {
        Path expected = folder.getRoot().toPath().resolve("createnotexists.edb");

        Storage storage = provider.create("createnotexists");

        assertThat(storage).isInstanceOf(FileStorage.class);
        assertThat(((FileStorage) storage).getStorageLocation()).isEqualTo(expected);
        // File is created by FileStorage#initialize
        assertThat(expected).doesNotExist();
    }

    @Test
    public void testCreateExists() throws Exception {
        folder.newFile("createexists.edb");

        assertThatThrownBy(() -> provider.create("createexists"))
                .isInstanceOf(StorageExistsException.class)
                .hasMessageContaining("createexists");

    }

}