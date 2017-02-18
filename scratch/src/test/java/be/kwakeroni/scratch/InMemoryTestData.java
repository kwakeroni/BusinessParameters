package be.kwakeroni.scratch;

import be.kwakeroni.parameters.backend.inmemory.factory.InMemoryBackendServiceFactory;
import be.kwakeroni.parameters.backend.inmemory.service.InMemoryBackend;
import be.kwakeroni.scratch.tv.Dag;
import be.kwakeroni.scratch.tv.MappedRangedTVGroup;
import be.kwakeroni.scratch.tv.MappedTVGroup;
import be.kwakeroni.scratch.tv.RangedTVGroup;
import be.kwakeroni.scratch.tv.SimpleTVGroup;
import be.kwakeroni.scratch.tv.Slot;

/**
 * (C) 2017 Maarten Van Puymbroeck
 */
public class InMemoryTestData implements TestData {

    private final InMemoryBackend backend;

    public InMemoryTestData(){
        this.backend = InMemoryBackendServiceFactory.getSingletonInstance();
        reset();
    }

    @Override
    public void close() throws Exception {

    }

    @Override
    public void reset() {
        this.backend.setGroupData(SimpleTVGroup.instance().getName(),
                SimpleTVGroup.getData(Dag.MAANDAG, Slot.atHour(20)));

        this.backend.setGroupData(MappedTVGroup.instance().getName(),
                MappedTVGroup.getData(Dag.ZATERDAG, "Samson",
                        Dag.ZONDAG, "Morgen Maandag"));

        this.backend.setGroupData(RangedTVGroup.instance().getName(),
                RangedTVGroup.getData(Slot.atHour(8), Slot.atHour(12), "Samson",
                        Slot.atHalfPast(20), Slot.atHour(22), "Morgen Maandag"));

        this.backend.setGroupData(MappedRangedTVGroup.instance().getName(),
                MappedRangedTVGroup.getData(
                        MappedRangedTVGroup.entryData(Dag.MAANDAG, Slot.atHalfPast(20), Slot.atHour(22), "Gisteren Zondag"),
                        MappedRangedTVGroup.entryData(Dag.ZATERDAG, Slot.atHour(8), Slot.atHour(12), "Samson"),
                        MappedRangedTVGroup.entryData(Dag.ZATERDAG, Slot.atHour(14), Slot.atHour(18), "Koers"),
                        MappedRangedTVGroup.entryData(Dag.ZONDAG, Slot.atHalfPast(20), Slot.atHour(22), "Morgen Maandag")
                ));
    }

}
