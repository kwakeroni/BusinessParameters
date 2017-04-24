package be.kwakeroni.parameters.basic.definition;

import be.kwakeroni.parameters.definition.api.builder.DefinitionBuilder;

/**
 * (C) 2017 Maarten Van Puymbroeck
 */
public interface SimpleDefinitionBuilder extends DefinitionBuilder {

    SimpleDefinitionBuilder withParameter(String name);

}
