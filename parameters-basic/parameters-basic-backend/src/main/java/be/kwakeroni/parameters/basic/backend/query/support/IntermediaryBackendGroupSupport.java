package be.kwakeroni.parameters.basic.backend.query.support;

import be.kwakeroni.parameters.backend.api.BackendGroup;
import be.kwakeroni.parameters.backend.api.query.BackendQuery;
import be.kwakeroni.parameters.backend.api.query.BackendWireFormatterContext;

/**
 * (C) 2017 Maarten Van Puymbroeck
 */
public abstract class IntermediaryBackendGroupSupport<Q extends BackendQuery<? extends Q, ?>, S, E> implements BackendGroup<Q, S, E> {

    private final BackendGroup<Q, S, E> subGroup;

    public IntermediaryBackendGroupSupport(BackendGroup<Q, S, E> subGroup) {
        this.subGroup = subGroup;
    }

    @Override
    public BackendQuery<? extends Q, ?> internalize(Object query, BackendWireFormatterContext context) {
        return context.internalize(this, query);
    }

    public BackendGroup<Q, S, E> getSubGroup() {
        return this.subGroup;
    }


    @Override
    public String getName() {
        return this.subGroup.getName();
    }

}
