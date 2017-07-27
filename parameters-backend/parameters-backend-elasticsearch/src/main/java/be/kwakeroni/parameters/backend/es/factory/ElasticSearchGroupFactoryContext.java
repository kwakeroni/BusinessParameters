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

    private Map<Class<?>, DefinitionVisitor<?>> factories = new HashMap<>();

    private <I extends DefinitionVisitor<?>> void register(Class<? super I> type, I visitor) {
        this.factories.put(type, visitor);
    }

    private <I extends DefinitionVisitor<?>> void unregister(Class<? super I> type, I visitor) {
        this.factories.remove(type, visitor);
    }


    public void register(ElasticSearchGroupFactory factory) {
        if (factory != null) {
            factory.visit(this::register);
        }
    }

    public void unregister(ElasticSearchGroupFactory factory) {
        if (factory != null) {
            factory.visit(this::unregister);
        }
    }

    @Override
    public <V extends DefinitionVisitor<ElasticSearchGroup>> V getVisitor(Class<V> type) {
        return Objects.requireNonNull(type.cast(this.factories.get(type)), () -> "Not found visitor of type " + type.getName());
    }
}
