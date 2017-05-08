package be.kwakeroni.parameters.backend.inmemory.factory;

import be.kwakeroni.parameters.backend.inmemory.api.InMemoryGroup;
import be.kwakeroni.parameters.definition.api.DefinitionVisitor;
import be.kwakeroni.parameters.definition.api.DefinitionVisitorContext;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Created by kwakeroni on 08/05/17.
 */
public class InMemoryBackendGroupFactoryContext implements DefinitionVisitorContext<InMemoryGroup> {

    private Map<Class<? extends DefinitionVisitor<?>>, DefinitionVisitor<?>> factories = new HashMap<>();

    public <I extends DefinitionVisitor<?>> void register(Class<I> type, DefinitionVisitor<InMemoryGroup> factory) {
        this.factories.put(type, type.cast(factory));
    }

    @Override
    public <V extends DefinitionVisitor<InMemoryGroup>> V getVisitor(Class<V> type) {
        return Objects.requireNonNull(type.cast(this.factories.get(type)), () -> "Not found visitor of type " + type.getName());
    }

}
