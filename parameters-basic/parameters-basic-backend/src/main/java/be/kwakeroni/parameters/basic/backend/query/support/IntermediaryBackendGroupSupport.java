package be.kwakeroni.parameters.basic.backend.query.support;

import be.kwakeroni.parameters.backend.api.BackendGroup;
import be.kwakeroni.parameters.backend.api.query.BackendQuery;
import be.kwakeroni.parameters.backend.api.query.BackendWireFormatterContext;
import be.kwakeroni.parameters.definition.api.ParameterGroupDefinition;

/**
 * (C) 2017 Maarten Van Puymbroeck
 */
public abstract class IntermediaryBackendGroupSupport<Q extends BackendQuery<? extends Q, ?>, BG extends BackendGroup<Q>> implements BackendGroup<Q> {

    private final ParameterGroupDefinition definition;
    private final BG subGroup;

    public IntermediaryBackendGroupSupport(ParameterGroupDefinition definition, BG subGroup) {
        this.definition = definition;
        this.subGroup = subGroup;
    }

    @Override
    public BackendQuery<? extends Q, ?> internalize(Object query, BackendWireFormatterContext context) {
        return context.internalize(this, query);
    }

    public BG getSubGroup() {
        return this.subGroup;
    }


    @Override
    public String getName() {
        return this.subGroup.getName();
    }

    @Override
    public ParameterGroupDefinition getDefinition() {
        return this.definition;
    }
}
