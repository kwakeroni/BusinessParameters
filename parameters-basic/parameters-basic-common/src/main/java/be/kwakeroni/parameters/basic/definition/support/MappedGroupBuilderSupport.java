package be.kwakeroni.parameters.basic.definition.support;

import be.kwakeroni.parameters.basic.definition.MappedGroupBuilder;
import be.kwakeroni.parameters.definition.api.GroupBuilder;
import be.kwakeroni.parameters.definition.api.GroupBuilderFinalizer;

import java.util.function.Function;

/**
 * Created by kwakeroni on 11.04.17.
 */
public abstract class MappedGroupBuilderSupport<G> implements MappedGroupBuilder<G> {

    private String keyParameter;
    private GroupBuilder<G> subGroup;

    @Override
    public MappedGroupBuilder<G> withKeyParameter(String name) {
        this.keyParameter = name;
        return this;
    }

    @Override
    public MappedGroupBuilder<G> mappingTo(GroupBuilder<G> subGroup) {
        this.subGroup = subGroup;
        return this;
    }

    protected String getKeyParameter() {
        return keyParameter;
    }

    protected G buildSubGroup(Function<GroupBuilderFinalizer<G>, GroupBuilderFinalizer<G>> theirFinalizer) {
        return subGroup.build(myFinalizer().andThen(theirFinalizer));
    }

    private Function<GroupBuilderFinalizer<G>, GroupBuilderFinalizer<G>> myFinalizer() {
        return builder -> builder.prependParameter(keyParameter);
    }

    @Override
    public G build(Function<GroupBuilderFinalizer<G>, GroupBuilderFinalizer<G>> finalizer) {
        return build(buildSubGroup(finalizer));
    }

    protected abstract G build(G subGroup);
}
