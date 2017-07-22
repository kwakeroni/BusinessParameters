package be.kwakeroni.parameters.backend.api.query;

import be.kwakeroni.parameters.backend.api.BackendGroup;

/**
 * (C) 2016 Maarten Van Puymbroeck
 */
public interface BackendWireFormatterContext {

    public <F extends BackendWireFormatter> F getWireFormatter(Class<F> type);

    public <Q> Q internalize(BackendGroup<Q> group, Object query);

}
