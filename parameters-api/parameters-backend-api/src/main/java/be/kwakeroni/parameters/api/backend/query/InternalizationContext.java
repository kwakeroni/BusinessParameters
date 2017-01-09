package be.kwakeroni.parameters.api.backend.query;

import be.kwakeroni.parameters.api.backend.BackendGroup;

/**
 * (C) 2016 Maarten Van Puymbroeck
 */
public interface InternalizationContext<Q> {

    Q internalize(BackendGroup<Q> group, Object query);
    <Int extends Internalizer> Int getInternalizer(Class<Int> type);
}
