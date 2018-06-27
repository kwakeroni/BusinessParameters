package be.kwakeroni.parameters.backend.api;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.function.Function;

public interface ConfigurationProperty<T> {

    public String getName();

    public T fromString(String value);

    public static ConfigurationProperty<String> ofString(String name) {
        return of(name, Function.identity());
    }

    public static ConfigurationProperty<Integer> ofInteger(String name) {
        return of(name, Integer::parseInt);
    }

    public static ConfigurationProperty<Path> ofPath(String name) {
        return of(name, Paths::get);
    }

    public static <T> ConfigurationProperty<T> of(String name, Function<String, T> parseFunction) {
        Objects.requireNonNull(name);
        Objects.requireNonNull(parseFunction);
        return new ConfigurationProperty<T>() {
            @Override
            public String getName() {
                return name;
            }

            @Override
            public T fromString(String value) {
                return parseFunction.apply(value);
            }
        };
    }
}
