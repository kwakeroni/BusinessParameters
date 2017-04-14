package be.kwakeroni.parameters.definition.api;

import java.util.function.Function;

/**
 * (C) 2017 Maarten Van Puymbroeck
 */
public interface GroupBuilder<G> {

    public G build();

    public GroupBuilder<G> finalize(Function<GroupBuilderFinalizer<G>, GroupBuilderFinalizer<G>> finalizer);

}
