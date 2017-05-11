package be.kwakeroni.parameters.adapter.jmx.api;

import be.kwakeroni.parameters.definition.api.DefinitionVisitor;

/**
 * Created by kwakeroni on 09/05/17.
 */
public interface JMXGroupMBeanFactory extends DefinitionVisitor<JMXGroupBuilder> {

    @SuppressWarnings("rawtypes")
    public Class<? extends DefinitionVisitor> getProvidedInterface();


}
