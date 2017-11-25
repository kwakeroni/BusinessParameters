package be.kwakeroni.parameters.basic.definition.builder;

import be.kwakeroni.parameters.basic.client.model.Simple;
import be.kwakeroni.parameters.definition.ext.DefinitionBuilder;

/**
 * (C) 2017 Maarten Van Puymbroeck
 */
public interface SimpleDefinitionBuilder extends DefinitionBuilder<Simple> {

    SimpleDefinitionBuilder withParameter(String name);

}
