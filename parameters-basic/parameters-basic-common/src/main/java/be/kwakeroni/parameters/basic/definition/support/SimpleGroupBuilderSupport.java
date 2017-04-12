package be.kwakeroni.parameters.basic.definition.support;

import be.kwakeroni.parameters.basic.definition.SimpleGroupBuilder;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Created by kwakeroni on 11.04.17.
 */
public abstract class SimpleGroupBuilderSupport<G> implements SimpleGroupBuilder<G> {

    private final Set<String> parameters = new LinkedHashSet<>();

    @Override
    public SimpleGroupBuilder<G> withParameter(String name) {
        this.parameters.add(name);
        return this;
    }

    protected Set<String> getParameters() {
        return parameters;
    }

}
