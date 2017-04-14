package be.kwakeroni.parameters.basic.definition.support;

import be.kwakeroni.parameters.basic.definition.SimpleGroupBuilder;
import be.kwakeroni.parameters.definition.api.GroupBuilderFinalizer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

/**
 * Created by kwakeroni on 11.04.17.
 */
public abstract class SimpleGroupBuilderSupport<G> implements SimpleGroupBuilder<G>, GroupBuilderFinalizer<G> {

    private final List<String> parameters = new ArrayList<>();
    private Function<GroupBuilderFinalizer<G>, GroupBuilderFinalizer<G>> finalizer;

    @Override
    public SimpleGroupBuilder<G> withParameter(String name) {
        appendParameter(name);
        return this;
    }

    @Override
    public GroupBuilderFinalizer<G> prependParameter(String name) {
        System.out.println("Prepending parameter " + name);
        parameters.add(0, name);
        return this;
    }

    @Override
    public GroupBuilderFinalizer<G> appendParameter(String name) {
        parameters.add(name);
        return this;
    }

    @Override
    public final G build(Function<GroupBuilderFinalizer<G>, GroupBuilderFinalizer<G>> finalizer) {
        finalizer.apply(this);
        return createGroup();
    }

    protected abstract G createGroup();

    protected Collection<String> getParameters() {
        return Collections.unmodifiableCollection(parameters);
    }

}
