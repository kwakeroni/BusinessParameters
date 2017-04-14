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
    private Function<GroupBuilderFinalizer<G>, GroupBuilderFinalizer<G>> finalizer = null;

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

    @Override
    public GroupBuilder<G> finalize(Function<GroupBuilderFinalizer<G>, GroupBuilderFinalizer<G>> theirFinalizer) {
        this.finalizer = (this.finalizer == null) ? theirFinalizer : this.finalizer.andThen(theirFinalizer);
        return this;
    }

    protected String getKeyParameter() {
        return keyParameter;
    }

    private GroupBuilder<G> getSubGroup() {
        return subGroup;
    }

    protected final G buildSubGroup() {
        return subGroup.finalize(finalizer()).build();
    }

    private Function<GroupBuilderFinalizer<G>, GroupBuilderFinalizer<G>> finalizer() {
        return (finalizer == null) ? myFinalizer() : myFinalizer().andThen(finalizer);
    }

    private Function<GroupBuilderFinalizer<G>, GroupBuilderFinalizer<G>> myFinalizer() {
        return builder -> builder.prependParameter(keyParameter);
    }

}
