package be.kwakeroni.scratch.tv;

import be.kwakeroni.parameters.backend.inmemory.api.GroupData;
import be.kwakeroni.parameters.backend.inmemory.support.DefaultEntryData;
import be.kwakeroni.parameters.basic.backend.inmemory.InmemorySimpleGroup;
import be.kwakeroni.parameters.client.api.model.Entry;
import be.kwakeroni.parameters.basic.client.model.Simple;
import be.kwakeroni.parameters.basic.client.support.Entries;
import be.kwakeroni.parameters.client.api.model.Parameter;
import be.kwakeroni.parameters.client.api.model.ParameterGroup;

/**
 * (C) 2016 Maarten Van Puymbroeck
 */
public class SimpleTVGroup implements ParameterGroup<Simple> {


    public static final SimpleTVGroup instance() {
        return new SimpleTVGroup();
    }

    @Override
    public String getName() {
        return NAME;
    }

    public static Parameter<Dag> DAY = new DefaultParameter<>("day", Dag.type);
    public static Parameter<Slot> SLOT = new DefaultParameter<>("slot", Slot.type);

    public static Entry entry(Dag dag, Slot slot) {
        return Entries.entryOf(DAY, dag, SLOT, slot);
    }

    // For test purposes
    public static final GroupData getData(Dag dag, Slot slot) {
        return new DefaultGroupData(
                INMEMORY_GROUP,
                DefaultEntryData.of(
                        DAY.getName(), dag.toString(),
                        SLOT.getName(), slot.toString()
                )
        );
    }
    public static final String NAME = "tv.simple";
    private static final InmemorySimpleGroup INMEMORY_GROUP = new InmemorySimpleGroup(NAME, true, DAY.getName(), SLOT.getName());

}
