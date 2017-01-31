package be.kwakeroni.parameters.backend.inmemory.api;

import java.util.function.Consumer;

/**
 * (C) 2017 Maarten Van Puymbroeck
 */
public interface EntryModification {

    EntryData getEntry();

    Consumer<EntryData> getModifier();

}
