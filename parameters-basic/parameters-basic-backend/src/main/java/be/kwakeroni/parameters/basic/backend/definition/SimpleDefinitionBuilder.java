package be.kwakeroni.parameters.basic.backend.definition;

import be.kwakeroni.parameters.backend.api.definition.BackendDefinitionBuilder;

/**
 * (C) 2017 Maarten Van Puymbroeck
 */
public interface SimpleDefinitionBuilder<Q> extends BackendDefinitionBuilder<Q> {

    SimpleDefinitionBuilder<Q> withParameter(String name);



}
