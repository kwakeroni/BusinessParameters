package be.kwakeroni.parameters.basic.definition.jmx;

import be.kwakeroni.parameters.adapter.jmx.api.JMXGroupBuilder;
import be.kwakeroni.parameters.adapter.jmx.api.JMXGroupMBeanFactory;
import be.kwakeroni.parameters.basic.definition.factory.MappedDefinitionVisitor;

import java.util.function.Consumer;

/**
 * Created by kwakeroni on 10/05/17.
 */
public class JMXMappedGroupMBeanFactory implements MappedDefinitionVisitor<JMXGroupBuilder>, JMXGroupMBeanFactory {

    @Override
    public void register(Registry registry) {
        registry.register(MappedDefinitionVisitor.class, this);
    }

    @Override
    public void unregister(Consumer<Class<?>> registry) {
        registry.accept(MappedDefinitionVisitor.class);
    }

    @Override
    public JMXGroupBuilder visit(Definition definition, JMXGroupBuilder subGroup) {

        subGroup.getQuery(Operations.GET_VALUE)
                .pushType(BasicJMXBackendWireFormatter.ACTION_TYPE_MAPPED)
                .prependParameter(definition.getKeyParameter());

        subGroup.getQuery(Operations.GET_ENTRY)
                .pushType(BasicJMXBackendWireFormatter.ACTION_TYPE_MAPPED)
                .prependParameter(definition.getKeyParameter());

        return subGroup;
    }
}
