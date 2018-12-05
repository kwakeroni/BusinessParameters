package be.kwakeroni.parameters.app.support;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class StaticContentFactory {

    private static final Logger LOG = LoggerFactory.getLogger(StaticContentFactory.class);

    private StaticContentFactory() {

    }

    public static StaticContent fromZip(Supplier<InputStream> zipFile, String indexFile, Path workDir) {
        LOG.info("Buffering web application into {}", workDir.toAbsolutePath());

        try {
            Files.createDirectories(workDir);
            unzip(zipFile, workDir);
        } catch (IOException exc) {
            throw new UncheckedIOException(exc);
        }

        return new StaticContent() {
            @Override
            public Path getContentDirectory() {
                return workDir;
            }

            @Override
            public String getIndexPage() {
                return indexFile;
            }
        };
    }


    private static void unzip(Supplier<InputStream> stream, java.nio.file.Path location) throws IOException {

        InputStream input = Objects.requireNonNull(stream.get(), "Could not obtain zip file");

        try (ZipInputStream zipStream = new ZipInputStream(input)) {
            for (ZipEntry entry = zipStream.getNextEntry(); entry != null; entry = zipStream.getNextEntry()) {
                java.nio.file.Path dest = location.resolve(entry.getName());
                if (entry.isDirectory()) {
                    Files.createDirectories(dest);
                } else {
                    Files.copy(zipStream, dest, StandardCopyOption.REPLACE_EXISTING);
                }
            }
            zipStream.closeEntry();
        }
    }

}
