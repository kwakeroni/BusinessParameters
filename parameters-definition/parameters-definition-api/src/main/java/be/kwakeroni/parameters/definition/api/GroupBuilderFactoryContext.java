package be.kwakeroni.parameters.definition.api;

/**
 * Created by kwakeroni on 10.04.17.
 */
public interface GroupBuilderFactoryContext<G> {

    public <GBF extends GroupBuilderFactory<G>> GBF getBuilder(Class<GBF> type);

}
