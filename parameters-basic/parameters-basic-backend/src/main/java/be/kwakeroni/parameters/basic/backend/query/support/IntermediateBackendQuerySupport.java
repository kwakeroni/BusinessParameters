package be.kwakeroni.parameters.basic.backend.query.support;

import be.kwakeroni.parameters.backend.api.query.BackendQuery;
import be.kwakeroni.parameters.backend.api.query.BackendWireFormatterContext;

/**
 * (C) 2017 Maarten Van Puymbroeck
 */
public abstract class IntermediateBackendQuerySupport<Q extends BackendQuery<Q, V>, V> implements BackendQuery<Q, V> {

    private final Q subQuery;

    public IntermediateBackendQuerySupport(Q subQuery) {
        this.subQuery = subQuery;
    }

    protected Q getSubQuery() {
        return subQuery;
    }

    @Override
    public Object externalizeValue(V value, BackendWireFormatterContext wireFormatterContext) {
        return subQuery.externalizeValue(value, wireFormatterContext);
    }

    @Override
    public V internalizeValue(Object value, BackendWireFormatterContext wireFormatterContext) {
        return subQuery.internalizeValue(value, wireFormatterContext);
    }
}
