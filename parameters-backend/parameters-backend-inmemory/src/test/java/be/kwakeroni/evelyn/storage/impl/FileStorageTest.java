package be.kwakeroni.evelyn.storage.impl;

import be.kwakeroni.evelyn.storage.StorageExistsException;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.UncheckedIOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class FileStorageTest {

    @ClassRule
    public static final TemporaryFolder FOLDER = new TemporaryFolder();
    private static final AtomicInteger FILE_COUNTER = new AtomicInteger();

    private Path storageFile;
    private FileStorage fileStorage;

    @Before
    public void setUp() {
        this.storageFile = FOLDER.getRoot().toPath().resolve("junit-" + FILE_COUNTER.incrementAndGet() + ".tmp");
        this.fileStorage = new FileStorage(this.storageFile);
    }

    @Test
    public void reportsFileAsSource() throws Exception {
        assertThat(this.fileStorage.getReference()).isEqualTo(this.storageFile.toUri().toString());
    }

    @Test
    public void createsTheStorageFile() throws Exception {
        assertThat(this.storageFile).doesNotExist();

        this.fileStorage.initialize();

        assertThat(this.storageFile).exists();
    }

    @Test
    public void throwsExceptionWhenStorageFileExists() throws Exception {

        Files.write(storageFile, Arrays.asList("Hello, I exist"));

        assertThatThrownBy(() -> this.fileStorage.initialize())
                .isInstanceOf(StorageExistsException.class);
    }

    @Test
    public void appendsToTheFile() throws Exception {
        Files.write(storageFile, Arrays.asList("Hello, I exist"));

        this.fileStorage.append("!evelyn-db");

        assertThat(Files.readAllLines(storageFile)).containsExactly(
                "Hello, I exist",
                "!evelyn-db"
        );
    }

    @Test
    public void throwsExceptionWhenAppendingToNonExistingFile() throws Exception {
        assertThatThrownBy(() -> this.fileStorage.append("!evelyn-db"))
                .hasMessageContaining(storageFile.toString());
    }

    @Test
    public void readsTheFile() throws Exception {
        Files.write(storageFile, Arrays.asList("!evelyn-db", "!version=x.y", "!data"));

        assertThat(fileStorage.read(Charset.defaultCharset()))
                .containsExactly(
                        "!evelyn-db",
                        "!version=x.y",
                        "!data"
                );
    }

    @Test
    public void readsDifferentEncoding() throws Exception {
        Files.write(storageFile, Arrays.asList("!evelyn-db", "!version=x.y", "!custom=CharactèreSpéciale", "!data"), Charset.forName("ISO-8859-1"));

        assertThat(fileStorage.read(Charset.forName("ISO-8859-1")))
                .containsExactly(
                        "!evelyn-db",
                        "!version=x.y",
                        "!custom=CharactèreSpéciale",
                        "!data"
                );
    }

    @Test
    public void propagatesIOExceptions() throws Exception {
        FileStorage fileStorage = new FileStorage(Paths.get("/not/existing"));

        assertThatThrownBy(fileStorage::initialize)
                .isInstanceOf(UncheckedIOException.class);

        assertThatThrownBy(() -> fileStorage.append("data"))
                .isInstanceOf(UncheckedIOException.class);

        assertThatThrownBy(() -> fileStorage.read(Charset.defaultCharset()))
                .isInstanceOf(UncheckedIOException.class);

    }
}
