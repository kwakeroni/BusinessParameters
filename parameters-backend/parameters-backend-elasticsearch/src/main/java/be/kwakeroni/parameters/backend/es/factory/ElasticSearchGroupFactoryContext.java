package be.kwakeroni.parameters.backend.es.factory;

import be.kwakeroni.parameters.backend.es.api.ElasticSearchGroup;
import be.kwakeroni.parameters.backend.es.api.ElasticSearchGroupFactory;
import be.kwakeroni.parameters.definition.api.DefinitionVisitor;
import be.kwakeroni.parameters.definition.api.DefinitionVisitorContext;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Created by kwakeroni on 05/05/17.
 */
public class ElasticSearchGroupFactoryContext implements DefinitionVisitorContext<ElasticSearchGroup> {

    private Map<Class<? extends DefinitionVisitor<?>>, DefinitionVisitor<?>> factories = new HashMap<>();

    private <I extends DefinitionVisitor<?>> void register(Class<I> type, DefinitionVisitor<ElasticSearchGroup> factory) {
        this.factories.put(type, type.cast(factory));
    }

    public void register(ElasticSearchGroupFactory factory) {
        register(factory.getProvidedInterface(), factory);
    }

    public void unregister(ElasticSearchGroupFactory factory) {
        if (factory != null) {
            this.factories.remove(factory.getProvidedInterface(), factory);
        }
    }

    @Override
    public <V extends DefinitionVisitor<ElasticSearchGroup>> V getVisitor(Class<V> type) {
        return Objects.requireNonNull(type.cast(this.factories.get(type)), () -> "Not found visitor of type " + type.getName());
    }
}
