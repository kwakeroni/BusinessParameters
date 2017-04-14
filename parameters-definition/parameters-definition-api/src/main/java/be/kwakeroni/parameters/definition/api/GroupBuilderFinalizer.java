package be.kwakeroni.parameters.definition.api;

/**
 * Created by kwakeroni on 13.04.17.
 */
public interface GroupBuilderFinalizer<G> {

    public GroupBuilderFinalizer<G> prependParameter(String name);

    public GroupBuilderFinalizer<G> appendParameter(String name);


}
