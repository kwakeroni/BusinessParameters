package be.kwakeroni.scratch;

import be.kwakeroni.parameters.backend.api.factory.BusinessParametersBackendFactory;
import be.kwakeroni.parameters.backend.inmemory.api.GroupData;
import be.kwakeroni.parameters.backend.inmemory.factory.InMemoryBackendServiceFactory;
import be.kwakeroni.parameters.backend.inmemory.service.InMemoryBackend;
import be.kwakeroni.scratch.tv.*;

import java.util.ArrayList;
import java.util.List;

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

    private void setGroupData(String groupName, GroupData data) {
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
                SimpleTVGroup.getData(Dag.MAANDAG, Slot.atHour(20)));

        setGroupData(MappedTVGroup.instance().getName(),
                MappedTVGroup.getData(Dag.ZATERDAG, "Samson",
                        Dag.ZONDAG, "Morgen Maandag"));

        setGroupData(RangedTVGroup.instance().getName(),
                RangedTVGroup.getData(Slot.atHour(8), Slot.atHour(12), "Samson",
                        Slot.atHalfPast(20), Slot.atHour(22), "Morgen Maandag"));

        setGroupData(MappedRangedTVGroup.instance().getName(),
                MappedRangedTVGroup.getData(
                        MappedRangedTVGroup.entryData(Dag.MAANDAG, Slot.atHalfPast(20), Slot.atHour(22), "Gisteren Zondag"),
                        MappedRangedTVGroup.entryData(Dag.ZATERDAG, Slot.atHour(8), Slot.atHour(12), "Samson"),
                        MappedRangedTVGroup.entryData(Dag.ZATERDAG, Slot.atHour(14), Slot.atHour(18), "Koers"),
                        MappedRangedTVGroup.entryData(Dag.ZONDAG, Slot.atHalfPast(20), Slot.atHour(22), "Morgen Maandag")
                ));
    }

}
