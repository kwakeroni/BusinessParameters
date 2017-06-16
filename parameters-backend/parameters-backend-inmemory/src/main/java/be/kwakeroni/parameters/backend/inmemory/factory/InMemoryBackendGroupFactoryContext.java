package be.kwakeroni.parameters.backend.inmemory.factory;

import be.kwakeroni.parameters.backend.inmemory.api.InMemoryGroup;
import be.kwakeroni.parameters.backend.inmemory.api.InMemoryGroupFactory;
import be.kwakeroni.parameters.definition.api.DefinitionVisitor;
import be.kwakeroni.parameters.definition.api.DefinitionVisitorContext;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Created by kwakeroni on 08/05/17.
 */
public class InMemoryBackendGroupFactoryContext implements DefinitionVisitorContext<InMemoryGroup> {

    private Map<Class<?>, Object> factories = new HashMap<>();

    private <I> void register(Class<? super I> type, I object) {
        this.factories.merge(type, object,
                (one, two) -> {
                    throw new IllegalStateException(String.format("Multiple JMXGroupMBeanFactory instances registered for %s (%one, %two)", type, one, two));
                });
    }

    public void register(InMemoryGroupFactory factory){
        factory.register(this::register);
    }

    public void unregister(InMemoryGroupFactory factory){
        if (factory != null) {
            factory.unregister(this.factories::remove);
        }
    }

    @Override
    public <V extends DefinitionVisitor<InMemoryGroup>> V getVisitor(Class<V> type) {
        return Objects.requireNonNull(type.cast(this.factories.get(type)), () -> "Not found visitor of type " + type.getName());
    }

}
