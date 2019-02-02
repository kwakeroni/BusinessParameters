package be.kwakeroni.scratch.env.inmemory;

import be.kwakeroni.parameters.backend.api.factory.BusinessParametersBackendFactory;
import be.kwakeroni.parameters.backend.inmemory.api.EntryData;
import be.kwakeroni.parameters.backend.inmemory.api.InMemoryGroup;
import be.kwakeroni.parameters.backend.inmemory.factory.InMemoryBackendServiceFactory;
import be.kwakeroni.parameters.backend.inmemory.fallback.TransientGroupDataStore;
import be.kwakeroni.parameters.basic.definition.factory.HistoricizedDefinitionVisitor;
import be.kwakeroni.parameters.basic.definition.factory.MappedDefinitionVisitor;
import be.kwakeroni.parameters.basic.definition.factory.RangedDefinitionVisitor;
import be.kwakeroni.parameters.basic.definition.factory.SimpleDefinitionVisitor;
import be.kwakeroni.parameters.basic.definition.inmemory.InMemoryHistoricizedGroupFactory;
import be.kwakeroni.parameters.basic.definition.inmemory.InMemoryMappedGroupFactory;
import be.kwakeroni.parameters.basic.definition.inmemory.InMemoryRangedGroupFactory;
import be.kwakeroni.parameters.basic.definition.inmemory.InMemorySimpleGroupFactory;
import be.kwakeroni.parameters.definition.api.DefinitionVisitorContext;
import be.kwakeroni.scratch.Contexts;
import be.kwakeroni.scratch.env.TestData;
import be.kwakeroni.scratch.tv.AbstractMappedRangedTVGroup;
import be.kwakeroni.scratch.tv.AbstractRangedTVGroup;
import be.kwakeroni.scratch.tv.Dag;
import be.kwakeroni.scratch.tv.HistoricizedTVGroup;
import be.kwakeroni.scratch.tv.MappedRangedFilterTVGroup;
import be.kwakeroni.scratch.tv.MappedRangedQueryTVGroup;
import be.kwakeroni.scratch.tv.MappedTVGroup;
import be.kwakeroni.scratch.tv.RangedFilterTVGroup;
import be.kwakeroni.scratch.tv.RangedQueryTVGroup;
import be.kwakeroni.scratch.tv.SimpleTVGroup;
import be.kwakeroni.scratch.tv.Slot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * (C) 2017 Maarten Van Puymbroeck
 */
public class TransientInMemoryTestData implements TestData {

    private final TransientGroupDataStore dataStore = new TransientGroupDataStore();
    private final List<String> groups = new ArrayList<>();

    public TransientInMemoryTestData() {
        InMemoryBackendServiceFactory.setDataStoreSupplier(() -> this.dataStore);
        reset();
    }

    @Override
    public void close() {

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
        this.dataStore.setEntries(groupName, data);
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

        setGroupData(HistoricizedTVGroup.instance().getName(),
                HistoricizedTVGroup.entryData("20181215", "20190315", "Winter Wonderland"),
                // Don't include for testing purposes: HistoricizedTVGroup.entryData("20190315", "20190615", "Soppy Spring Soap"),
                HistoricizedTVGroup.entryData("20190615", "20190915", "Summer Standup Show"),
                HistoricizedTVGroup.entryData("20190915", "20191215", "Authentic Autumn Awards")
        );

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
            RangedDefinitionVisitor.class, new InMemoryRangedGroupFactory(),
            HistoricizedDefinitionVisitor.class, new InMemoryHistoricizedGroupFactory()
    );

}
