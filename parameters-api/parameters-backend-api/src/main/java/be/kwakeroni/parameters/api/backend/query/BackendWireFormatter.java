package be.kwakeroni.parameters.api.backend.query;

import be.kwakeroni.parameters.api.backend.BackendGroup;

import java.util.Optional;

/**
 * (C) 2016 Maarten Van Puymbroeck
 */
public interface BackendWireFormatter {

    public <Q> Optional<Q> tryInternalize(BackendGroup<Q> group, Object query, BackendWireFormatterContext<Q> context);

}
