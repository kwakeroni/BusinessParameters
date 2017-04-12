package be.kwakeroni.parameters.basic.definition;

import be.kwakeroni.parameters.definition.api.GroupBuilder;

/**
 * (C) 2017 Maarten Van Puymbroeck
 */
public interface SimpleGroupBuilder<G> extends GroupBuilder<G> {

    SimpleGroupBuilder<G> withParameter(String name);

}
