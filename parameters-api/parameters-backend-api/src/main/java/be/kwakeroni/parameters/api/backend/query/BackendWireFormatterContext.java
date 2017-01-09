package be.kwakeroni.parameters.api.backend.query;

import be.kwakeroni.parameters.api.backend.BackendGroup;

/**
 * (C) 2016 Maarten Van Puymbroeck
 */
public interface BackendWireFormatterContext<Q> {

    Q internalize(BackendGroup<Q> group, Object query);
    <F extends BackendWireFormatter> F getWireFormatter(Class<F> type);
}
