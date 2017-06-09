package be.kwakeroni.parameters.adapter.jmx;

import be.kwakeroni.parameters.adapter.jmx.api.JMXGroupBuilder;
import be.kwakeroni.parameters.adapter.jmx.api.JMXGroupMBeanFactory;
import be.kwakeroni.parameters.definition.api.DefinitionVisitor;
import be.kwakeroni.parameters.definition.api.DefinitionVisitorContext;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Created by kwakeroni on 09/05/17.
 */
public class JMXGroupMBeanFactoryContext implements DefinitionVisitorContext<JMXGroupBuilder> {


    private Map<Class<? extends DefinitionVisitor<?>>, DefinitionVisitor<?>> factories = new HashMap<>();

    private <I extends DefinitionVisitor<?>> void register(Class<I> type, DefinitionVisitor<JMXGroupBuilder> factory) {
        this.factories.put(type, type.cast(factory));
    }

    public void register(JMXGroupMBeanFactory factory){
        register(factory.getProvidedInterface(), factory);
    }

    public void unregister(JMXGroupMBeanFactory factory){
        this.factories.remove(factory.getProvidedInterface(), factory);
    }

    @Override
    public <V extends DefinitionVisitor<JMXGroupBuilder>> V getVisitor(Class<V> type) {
        return Objects.requireNonNull(type.cast(this.factories.get(type)), () -> "Not found visitor of type " + type.getName());
    }

}
