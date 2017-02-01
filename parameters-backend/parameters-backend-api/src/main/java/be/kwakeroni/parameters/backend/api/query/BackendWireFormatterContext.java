package be.kwakeroni.parameters.backend.api.query;

import be.kwakeroni.parameters.backend.api.BackendGroup;

/**
 * (C) 2016 Maarten Van Puymbroeck
 */
public interface BackendWireFormatterContext<Q> {

    public Q internalize(BackendGroup<Q, ?, ?> group, Object query);
    public <F extends BackendWireFormatter> F getWireFormatter(Class<F> type);
}
