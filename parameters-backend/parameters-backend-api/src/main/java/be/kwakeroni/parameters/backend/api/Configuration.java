package be.kwakeroni.parameters.backend.api;

import java.util.Optional;

public interface Configuration {

    public Optional<String> getStringParameter(String name);

    public default <T> Optional<T> get(ConfigurationProperty<T> property) {
        return getStringParameter(property.getName())
                .map(property::fromString);
    }

}
