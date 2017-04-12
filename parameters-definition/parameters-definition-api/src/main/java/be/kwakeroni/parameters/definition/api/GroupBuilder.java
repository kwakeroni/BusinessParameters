package be.kwakeroni.parameters.definition.api;

/**
 * (C) 2017 Maarten Van Puymbroeck
 */
public interface GroupBuilder<G> {

    public G createGroup(GroupBuilderFactoryContext<G> context);

}
