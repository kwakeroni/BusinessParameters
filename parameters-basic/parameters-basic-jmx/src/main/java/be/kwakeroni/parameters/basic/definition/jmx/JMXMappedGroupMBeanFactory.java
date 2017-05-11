package be.kwakeroni.parameters.basic.definition.jmx;

import be.kwakeroni.parameters.adapter.jmx.api.JMXGroupBuilder;
import be.kwakeroni.parameters.adapter.jmx.api.JMXGroupMBeanFactory;
import be.kwakeroni.parameters.basic.definition.factory.MappedDefinitionVisitor;
import be.kwakeroni.parameters.definition.api.DefinitionVisitor;

/**
 * Created by kwakeroni on 10/05/17.
 */
public class JMXMappedGroupMBeanFactory implements MappedDefinitionVisitor<JMXGroupBuilder>, JMXGroupMBeanFactory {

    @Override
    @SuppressWarnings("rawtypes")
    public Class<? extends DefinitionVisitor> getProvidedInterface() {
        return MappedDefinitionVisitor.class;
    }

    @Override
    public JMXGroupBuilder visit(Definition definition, JMXGroupBuilder subGroup) {

        subGroup.getQuery(Operations.GET_VALUE)
                .prependParameter(definition.getKeyParameter());

        subGroup.getQuery(Operations.GET_ENTRY)
                .prependParameter(definition.getKeyParameter());

        return subGroup;
    }
}
