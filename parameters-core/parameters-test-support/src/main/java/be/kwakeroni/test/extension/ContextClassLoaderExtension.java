package be.kwakeroni.test.extension;

import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
import org.junit.jupiter.api.extension.BeforeTestExecutionCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

public class ContextClassLoaderExtension extends ExtensionSupport implements BeforeTestExecutionCallback, AfterTestExecutionCallback {


    @Override
    public void beforeTestExecution(ExtensionContext context) {

        URL[] urls =
                getFields(context.getRequiredTestInstance())
                        .filter(annotatedBy(AddToClasspath.class))
                        .map(InstanceField::get)
                        .map(this::toURL)
                        .toArray(URL[]::new);


        ClassLoader original = Thread.currentThread().getContextClassLoader();
        State.ORIGINAL_CLASSLOADER.set(context, original);
        Thread.currentThread().setContextClassLoader(new URLClassLoader(urls, original));
    }

    private URL toURL(Object o) {
        try {
            if (o instanceof File) {
                return ((File) o).toURI().toURL();
            } else if (o instanceof Path) {
                return ((Path) o).toUri().toURL();
            } else if (o instanceof URL) {
                return (URL) o;
            } else if (o instanceof String) {
                return new URL((String) o);
            } else {
                throw new IllegalStateException("Could not convert to URL: " + o);
            }
        } catch (IOException exc) {
            throw new UncheckedIOException(exc);
        }
    }

    @Override
    public void afterTestExecution(ExtensionContext context) {
        ClassLoader original = State.ORIGINAL_CLASSLOADER.get(context, ClassLoader.class);
        Thread.currentThread().setContextClassLoader(original);
    }

    @Target({FIELD, PARAMETER})
    @Retention(RUNTIME)
    public static @interface AddToClasspath {
    }
}
