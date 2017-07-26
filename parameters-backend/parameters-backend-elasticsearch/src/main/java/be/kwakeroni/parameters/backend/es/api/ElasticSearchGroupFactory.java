package be.kwakeroni.parameters.backend.es.api;

import be.kwakeroni.parameters.definition.api.DefinitionVisitor;

/**
 * Created by kwakeroni on 05/05/17.
 */
public interface ElasticSearchGroupFactory extends DefinitionVisitor<ElasticSearchGroup> {

    public void visit(Registry registry);

    @FunctionalInterface
    public static interface Registry {
        public <I extends ElasticSearchGroupFactory> void register(Class<? super I> type, I formatter);
    }

}
