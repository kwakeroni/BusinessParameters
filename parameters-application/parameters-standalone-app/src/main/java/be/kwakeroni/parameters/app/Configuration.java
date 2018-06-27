package be.kwakeroni.parameters.app;

import java.util.Optional;
import java.util.OptionalInt;

interface Configuration {

    public Optional<String> getWorkDirectory();

    public OptionalInt getPort();

    public Optional<String> getContextPath();

}
