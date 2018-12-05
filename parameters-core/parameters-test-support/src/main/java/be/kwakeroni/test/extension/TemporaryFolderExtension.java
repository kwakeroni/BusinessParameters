package be.kwakeroni.test.extension;

import org.junit.Rule;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.nio.file.Path;
import java.nio.file.Paths;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

public class TemporaryFolderExtension extends ExtensionSupport implements BeforeEachCallback, AfterEachCallback {

    @Override
    public void beforeEach(ExtensionContext context) {
        getFields(context.getRequiredTestInstance())
                .filter(annotatedBy(TemporaryFolder.class))
                .forEach(field -> field.set(createTemporaryFolder(field.getType())));
    }

    @Override
    public void afterEach(ExtensionContext context) {
        getFields(context.getRequiredTestInstance())
                .filter(annotatedBy(TemporaryFolder.class))
                .forEach(field -> deleteTemporaryFolder(field.get()));
    }

    @Target({FIELD, PARAMETER})
    @Retention(RUNTIME)
    public static @interface TemporaryFolder {
    }


    private Object createTemporaryFolder(Class<?> type) {
        try {
            return toValue(type, createTemporaryFolderIn(null));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private Object toValue(Class<?> type, Path folder) {
        if (type == File.class) {
            return folder.toFile();
        } else if (type == Path.class) {
            return folder;
        } else if (type == String.class) {
            return folder.toAbsolutePath().toString();
        }
        throw new IllegalArgumentException("Cannot convert temporary folder to type " + type.getName());
    }

    private void deleteTemporaryFolder(Object folder) {
        delete(fromValue(folder));
    }

    private Path fromValue(Object folder) {
        if (folder instanceof File) {
            return ((File) folder).toPath();
        } else if (folder instanceof Path) {
            return (Path) folder;
        } else if (folder instanceof String) {
            return Paths.get((String) folder);
        }
        throw new IllegalArgumentException("Cannot convert temporary folder to path " + folder);
    }


    private Path createTemporaryFolderIn(File parentFolder) throws IOException {
        File createdFolder = File.createTempFile("junit", "", parentFolder);
        createdFolder.delete();
        createdFolder.mkdir();
        return createdFolder.toPath();
    }

    /**
     * Delete all files and folders under the temporary folder. Usually not
     * called directly, since it is automatically applied by the {@link Rule}
     */
    public void delete(Path path) {
        File folder = path.toFile();
        if (folder != null) {
            recursiveDelete(folder);
        }
    }

    private void recursiveDelete(File file) {
        File[] files = file.listFiles();
        if (files != null) {
            for (File each : files) {
                recursiveDelete(each);
            }
        }
        file.delete();
    }
}
