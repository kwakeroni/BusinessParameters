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

    private Map<Class<? extends DefinitionVisitor<?>>, DefinitionVisitor<?>> factories = new HashMap<>();

    private <I extends DefinitionVisitor<?>> void register(Class<I> type, DefinitionVisitor<InMemoryGroup> factory) {
        this.factories.put(type, type.cast(factory));
    }

    public void register(InMemoryGroupFactory factory){
        register(factory.getProvidedInterface(), factory);
    }

    public void unregister(InMemoryGroupFactory factory){
        this.factories.remove(factory.getProvidedInterface(), factory);
    }

    @Override
    public <V extends DefinitionVisitor<InMemoryGroup>> V getVisitor(Class<V> type) {
        return Objects.requireNonNull(type.cast(this.factories.get(type)), () -> "Not found visitor of type " + type.getName());
    }

}
