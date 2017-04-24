package be.kwakeroni.parameters.basic.definition;

import be.kwakeroni.parameters.definition.api.builder.DefinitionBuilder;

/**
 * (C) 2017 Maarten Van Puymbroeck
 */
public interface MappedDefinitionBuilder extends DefinitionBuilder {

    public MappedDefinitionBuilder withKeyParameter(String name);

    public MappedDefinitionBuilder mappingTo(DefinitionBuilder subGroup);

}
