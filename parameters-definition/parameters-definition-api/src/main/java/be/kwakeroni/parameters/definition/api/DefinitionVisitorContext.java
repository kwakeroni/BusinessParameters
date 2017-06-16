package be.kwakeroni.parameters.definition.api;

/**
 * Created by kwakeroni on 10.04.17.
 */
public interface DefinitionVisitorContext<G> {

    public <V extends DefinitionVisitor<G>> V getVisitor(Class<V> type);

}
