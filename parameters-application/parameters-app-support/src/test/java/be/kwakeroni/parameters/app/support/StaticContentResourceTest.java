package be.kwakeroni.parameters.app.support;

import be.kwakeroni.test.extension.TemporaryFolderExtension;
import be.kwakeroni.test.extension.TemporaryFolderExtension.TemporaryFolder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assumptions.assumeFalse;

@ExtendWith(TemporaryFolderExtension.class)
class StaticContentResourceTest {

    @TemporaryFolder
    Path tempFolder;

    @BeforeEach
    void setUp() {

    }

    @Test
    @DisplayName("Prefers probeContentType to determine content type")
    void testProbeContentType() throws Exception {
        Path testFile = tempFolder.resolve("test.css");
        Files.write(testFile, Arrays.asList("body { font-weight: bold; }"));
        assumeFalse(Files.probeContentType(testFile) == null, "Files.probeContentType appears to be unsupported on this OS");

        StaticContentResource resource = new StaticContentResource(tempFolder, "index.html") {
        };

        String contentType = resource.mimeType(testFile);
        assertThat(contentType).isEqualTo("text/css");
    }

    @Test
    @DisplayName("Falls back to extension mapping")
    void testExtensionMapping() throws Exception {
        Path testFile = tempFolder.resolve("test.css");
        Files.write(testFile, Arrays.asList("body { font-weight: bold; }"));

        StaticContentResource resource = new StaticContentResource(tempFolder, "index.html") {
            @Override
            String probeContentType(Path resource) throws Exception {
                return null;
            }
        };

        String contentType = resource.mimeType(testFile);
        assertThat(contentType).isEqualTo("text/css");
    }


}
