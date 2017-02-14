package be.kwakeroni.parameters.basic.backend.definition;

import be.kwakeroni.parameters.backend.api.definition.BackendDefinitionBuilder;
import be.kwakeroni.parameters.backend.api.definition.BackendDefinitionBuilderFactory;

/**
 * (C) 2017 Maarten Van Puymbroeck
 */
public interface BasicBackendDefinitionBuilderFactory<Q> extends BackendDefinitionBuilderFactory<Q> {

    SimpleDefinitionBuilder<Q> newGroup();
    MappedDefinitionBuilder<Q> mapped(BackendDefinitionBuilder<Q> builder);
    RangedDefinitionBuilder<Q> ranged(BackendDefinitionBuilder<Q> builder);

}
