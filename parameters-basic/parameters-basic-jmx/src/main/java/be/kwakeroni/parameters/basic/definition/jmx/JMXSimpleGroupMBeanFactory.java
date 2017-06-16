package be.kwakeroni.parameters.basic.definition.jmx;

import be.kwakeroni.parameters.adapter.jmx.api.JMXGroupBuilder;
import be.kwakeroni.parameters.adapter.jmx.api.JMXGroupMBeanFactory;
import be.kwakeroni.parameters.basic.definition.factory.SimpleDefinitionVisitor;

import java.util.function.Consumer;

/**
 * Created by kwakeroni on 09/05/17.
 */
public class JMXSimpleGroupMBeanFactory implements SimpleDefinitionVisitor<JMXGroupBuilder>, JMXGroupMBeanFactory {

    @Override
    public void register(Registry registry) {
        registry.register(SimpleDefinitionVisitor.class, this);
    }

    @Override
    public void unregister(Consumer<Class<?>> registry) {
        registry.accept(SimpleDefinitionVisitor.class);
    }

    @Override
    public JMXGroupBuilder visit(Definition definition) {

        JMXGroupBuilder builder = JMXGroupBuilder
                .withName(definition.getName())
                .withParameters(definition.getParameters());

        builder.addQuery(Operations.GET_VALUE)
                .pushType(BasicJMXBackendWireFormatter.ACTION_TYPE_VALUE)
                .withName("getValue")
                .withDescription("retrieve a parameter value for the selected entry")
                .appendParameter("parameter", definition.getParameters());

        builder.addQuery(Operations.GET_ENTRY)
                .pushType(BasicJMXBackendWireFormatter.ACTION_TYPE_ENTRY)
                .withName("getEntry")
                .withDescription("retrieve the selected entry");

        return builder;
    }
}
