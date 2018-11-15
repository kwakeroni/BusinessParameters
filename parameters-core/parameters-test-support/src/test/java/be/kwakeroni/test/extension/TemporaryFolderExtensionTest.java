package be.kwakeroni.test.extension;

import be.kwakeroni.test.extension.TemporaryFolderExtension.TemporaryFolder;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@ExtendWith(TemporaryFolderExtension.class)
class TemporaryFolderExtensionTest {

    private static List<Path> createdPaths;

    private static void addCreatedPath(Path path) {
        if (path != null) {
            assertThat(path).exists();
            createdPaths.add(path);
        }
    }

    @TemporaryFolder
    Path temporaryPath;
    @TemporaryFolder
    File temporaryFile;
    @TemporaryFolder
    String temporaryLocation;

    @BeforeAll
    static void clearStaticState() {
        createdPaths = new ArrayList<>();
    }

    @AfterAll
    static void checkAndClearStaticState() {
        createdPaths.stream().forEach(path -> assertThat(path).doesNotExist());
        clearStaticState();
    }

    @AfterEach
    void logCreatedPaths() {
        addCreatedPath(temporaryPath);
        addCreatedPath(temporaryFile.toPath());
        addCreatedPath(Paths.get(temporaryLocation));
    }

    @RepeatedTest(3)
    void createsTemporaryFolders() {
        testCreatedFolders();
        testDeletedFolders();
    }

    void testDeletedFolders() {
        for (Path path : createdPaths) {
            assertThat(path).doesNotExist();
        }
    }

    void testCreatedFolders() {
        assertThat(temporaryPath)
                .isNotNull()
                .exists();
        test(temporaryPath);

        assertThat(temporaryFile)
                .isNotNull()
                .exists();
        test(temporaryFile.toPath());

        assertThat(temporaryLocation).isNotNull();
        test(Paths.get(temporaryLocation));
    }


    @Nested
    class NestedTest {

        @RepeatedTest(3)
        void createsTemporaryFolders() {
            testCreatedFolders();
            testDeletedFolders();
        }

    }

    private static void test(Path path) {
        assertThat(path).exists();

        try {
            Files.write(path.resolve("test.txt"), Collections.singletonList("test"));
            Files.createDirectories(path.resolve("subdir"));
            Files.write(path.resolve("subdir").resolve("test.txt"), Collections.singletonList("test2"));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }

        assertThat(path.resolve("subdir")).exists();
        assertThat(path.resolve("test.txt")).hasContent("test");
        assertThat(path.resolve("subdir/test.txt")).hasContent("test2");
    }
}