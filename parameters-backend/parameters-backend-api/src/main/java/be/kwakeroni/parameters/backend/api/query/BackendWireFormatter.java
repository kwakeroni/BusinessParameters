package be.kwakeroni.parameters.backend.api.query;

import be.kwakeroni.parameters.backend.api.BackendGroup;

import java.util.Optional;

/**
 * (C) 2016 Maarten Van Puymbroeck
 */
public interface BackendWireFormatter {

    public <Q> Optional<Q> tryInternalize(BackendGroup<Q, ?, ?> group, Object query, BackendWireFormatterContext context);

}
