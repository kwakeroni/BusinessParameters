package be.kwakeroni.parameters.definition.api.factory;

/**
 * Created by kwakeroni on 10.04.17.
 */
public interface GroupFactoryContext<G> {

    public <GBF extends GroupFactory<G>> GBF getFactory(Class<GBF> type);

}
