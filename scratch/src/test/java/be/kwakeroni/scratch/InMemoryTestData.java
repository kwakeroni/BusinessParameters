package be.kwakeroni.scratch;

import be.kwakeroni.parameters.backend.api.factory.BusinessParametersBackendFactory;
import be.kwakeroni.parameters.backend.inmemory.api.EntryData;
import be.kwakeroni.parameters.backend.inmemory.api.InMemoryGroup;
import be.kwakeroni.parameters.backend.inmemory.factory.InMemoryBackendServiceFactory;
import be.kwakeroni.parameters.backend.inmemory.service.InMemoryBackend;
import be.kwakeroni.parameters.basic.definition.factory.MappedDefinitionVisitor;
import be.kwakeroni.parameters.basic.definition.factory.RangedDefinitionVisitor;
import be.kwakeroni.parameters.basic.definition.factory.SimpleDefinitionVisitor;
import be.kwakeroni.parameters.basic.definition.inmemory.InMemoryMappedGroupFactory;
import be.kwakeroni.parameters.basic.definition.inmemory.InMemoryRangedGroupFactory;
import be.kwakeroni.parameters.basic.definition.inmemory.InMemorySimpleGroupFactory;
import be.kwakeroni.parameters.definition.api.ParameterGroupDefinition;
import be.kwakeroni.parameters.definition.api.DefinitionVisitorContext;
import be.kwakeroni.scratch.tv.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * (C) 2017 Maarten Van Puymbroeck
 */
public class InMemoryTestData implements TestData {

    private final InMemoryBackend backend;
    private final List<String> groups = new ArrayList<>();

    public InMemoryTestData() {
        this.backend = InMemoryBackendServiceFactory.getSingletonInstance();
        reset();
    }

    @Override
    public void close() throws Exception {

    }

    @Override
    public boolean acceptBackend(BusinessParametersBackendFactory factory) {
        return factory instanceof InMemoryBackendServiceFactory;
    }

    @Override
    public boolean hasDataForGroup(String name) {
        return this.groups.contains(name);
    }

    private void setGroupData(String group1Name, String group2Name, EntryData... data) {
        setGroupData(group1Name, Arrays.asList(data));
        setGroupData(group2Name, Arrays.asList(data));
    }

    private void setGroupData(String groupName, EntryData... data) {
        setGroupData(groupName, Arrays.asList(data));
    }

    private void setGroupData(String groupName, Collection<EntryData> data) {
        this.backend.setGroupData(groupName, data);
        this.groups.add(groupName);
    }

    @Override
    public void notifyModifiedGroup(String name) {

    }

    @Override
    public void reset() {
        this.groups.clear();

        setGroupData(SimpleTVGroup.instance().getName(),
                SimpleTVGroup.getEntryData(Dag.MAANDAG, Slot.atHour(20)));

        setGroupData(MappedTVGroup.instance().getName(),
                MappedTVGroup.entryData(Dag.ZATERDAG, "Samson"),
                MappedTVGroup.entryData(Dag.ZONDAG, "Morgen Maandag"));

        setGroupData(RangedFilterTVGroup.instance().getName(), RangedQueryTVGroup.instance().getName(),
                AbstractRangedTVGroup.entryData(Slot.atHour(8), Slot.atHour(12), "Samson"),
                AbstractRangedTVGroup.entryData(Slot.atHalfPast(20), Slot.atHour(22), "Morgen Maandag"));

        setGroupData(MappedRangedFilterTVGroup.instance().getName(), MappedRangedQueryTVGroup.instance().getName(),
                AbstractMappedRangedTVGroup.entryData(Dag.MAANDAG, Slot.atHalfPast(20), Slot.atHour(22), "Gisteren Zondag"),
                AbstractMappedRangedTVGroup.entryData(Dag.ZATERDAG, Slot.atHour(8), Slot.atHour(12), "Samson"),
                AbstractMappedRangedTVGroup.entryData(Dag.ZATERDAG, Slot.atHour(14), Slot.atHour(18), "Koers"),
                AbstractMappedRangedTVGroup.entryData(Dag.ZONDAG, Slot.atHalfPast(20), Slot.atHour(22), "Morgen Maandag")
        );
    }

    public static DefinitionVisitorContext<InMemoryGroup> FACTORY_CONTEXT = Contexts.of(
            SimpleDefinitionVisitor.class, new InMemorySimpleGroupFactory(),
            MappedDefinitionVisitor.class, new InMemoryMappedGroupFactory(),
            RangedDefinitionVisitor.class, new InMemoryRangedGroupFactory()
    );

}
