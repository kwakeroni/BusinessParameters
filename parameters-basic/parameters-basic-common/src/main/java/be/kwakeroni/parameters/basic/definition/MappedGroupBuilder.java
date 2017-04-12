package be.kwakeroni.parameters.basic.definition;

import be.kwakeroni.parameters.definition.api.GroupBuilder;

/**
 * (C) 2017 Maarten Van Puymbroeck
 */
public interface MappedGroupBuilder<G> extends GroupBuilder<G> {

    public MappedGroupBuilder<G> withKeyParameter(String name);

    public MappedGroupBuilder<G> mappingTo(GroupBuilder<G> subGroup);

}
