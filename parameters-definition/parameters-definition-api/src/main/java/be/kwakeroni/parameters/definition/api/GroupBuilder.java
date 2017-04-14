package be.kwakeroni.parameters.definition.api;

import java.util.function.Function;

/**
 * (C) 2017 Maarten Van Puymbroeck
 */
public interface GroupBuilder<G> {

    public default G build() {
        return this.build(Function.identity());
    }

    public G build(Function<GroupBuilderFinalizer<G>, GroupBuilderFinalizer<G>> finalizer);

}
