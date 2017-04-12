package be.kwakeroni.parameters.basic.definition.support;

import be.kwakeroni.parameters.basic.definition.MappedGroupBuilder;
import be.kwakeroni.parameters.definition.api.GroupBuilder;

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

    protected GroupBuilder<G> getSubGroup() {
        return subGroup;
    }
}
