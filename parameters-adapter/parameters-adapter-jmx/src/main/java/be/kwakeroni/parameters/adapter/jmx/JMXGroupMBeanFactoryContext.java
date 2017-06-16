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

    private Map<Class<?>, Object> factories = new HashMap<>();

    private <I> void register(Class<? super I> type, I object) {
        this.factories.merge(type, object,
                (one, two) -> {
                    throw new IllegalStateException(String.format("Multiple JMXGroupMBeanFactory instances registered for %s (%one, %two)", type, one, two));
                });
    }

    public <I extends JMXGroupMBeanFactory> void register(I factory) {
        factory.register(this::register);
    }

    public void unregister(JMXGroupMBeanFactory factory) {
        if (factory != null) {
            factory.unregister(factories::remove);
        }
    }

    private <I> I get(Class<I> type) {
        return type.cast(this.factories.get(type));
    }

    @Override
    public <V extends DefinitionVisitor<JMXGroupBuilder>> V getVisitor(Class<V> type) {
        return Objects.requireNonNull(get(type), () -> "Not found visitor of type " + type.getName());
    }

}
