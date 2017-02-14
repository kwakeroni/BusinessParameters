package be.kwakeroni.parameters.basic.backend.inmemory;

import be.kwakeroni.parameters.backend.api.BackendGroup;
import be.kwakeroni.parameters.backend.api.definition.BackendDefinitionBuilder;
import be.kwakeroni.parameters.backend.inmemory.api.EntryData;
import be.kwakeroni.parameters.backend.inmemory.api.GroupData;
import be.kwakeroni.parameters.backend.inmemory.api.InMemoryQuery;
import be.kwakeroni.parameters.basic.backend.definition.BasicBackendDefinitionBuilderFactory;
import be.kwakeroni.parameters.basic.backend.definition.MappedDefinitionBuilder;
import be.kwakeroni.parameters.basic.backend.definition.RangedDefinitionBuilder;
import be.kwakeroni.parameters.basic.backend.definition.SimpleDefinitionBuilder;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * (C) 2017 Maarten Van Puymbroeck
 */
public class InMemoryBasicDefinitionBuilder implements BasicBackendDefinitionBuilderFactory<BackendGroup<InMemoryQuery<?>, GroupData, EntryData>> {

    @Override
    public SimpleDefinitionBuilder<BackendGroup<InMemoryQuery<?>, GroupData, EntryData>> newGroup() {
        return new SimpleBuilder();
    }

    @Override
    public MappedDefinitionBuilder<BackendGroup<InMemoryQuery<?>, GroupData, EntryData>> mapped(BackendDefinitionBuilder<BackendGroup<InMemoryQuery<?>, GroupData, EntryData>> builder) {
        throw new UnsupportedOperationException();
    }

    @Override
    public RangedDefinitionBuilder<BackendGroup<InMemoryQuery<?>, GroupData, EntryData>> ranged(BackendDefinitionBuilder<BackendGroup<InMemoryQuery<?>, GroupData, EntryData>> builder) {
        throw new UnsupportedOperationException();
    }



    private static final class SimpleBuilder
        implements SimpleDefinitionBuilder<BackendGroup<InMemoryQuery<?>, GroupData, EntryData>>{

        public List<String> parameters = new ArrayList<>();

        @Override
        public SimpleDefinitionBuilder<BackendGroup<InMemoryQuery<?>, GroupData, EntryData>> withParameter(String name) {
            parameters.add(name);
            return this;
        }

        @Override
        public BackendGroup<InMemoryQuery<?>, GroupData, EntryData> withGroupName(String groupName) {
            return new InmemorySimpleGroup(groupName, false, new HashSet<>(parameters));
        }
    }

    private static final class MappedBuilder
        implements MappedDefinitionBuilder<BackendGroup<InMemoryQuery<?>, GroupData, EntryData>>{
        @Override
        public BackendGroup<InMemoryQuery<?>, GroupData, EntryData> withGroupName(String groupName) {
            return new InmemoryMappedGroup()
        }
    }
}
