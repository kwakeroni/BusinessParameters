package be.kwakeroni.scratch.tv;

import be.kwakeroni.parameters.backend.inmemory.api.EntryData;
import be.kwakeroni.parameters.backend.inmemory.support.DefaultEntryData;
import be.kwakeroni.parameters.basic.backend.es.ElasticSearchSimpleGroup;
import be.kwakeroni.parameters.basic.backend.inmemory.InmemorySimpleGroup;
import be.kwakeroni.parameters.basic.client.support.Entries;
import be.kwakeroni.parameters.client.api.model.Entry;
import be.kwakeroni.scratch.tv.definition.SimpleTV;
import be.kwakeroni.test.factory.TestMap;

/**
 * (C) 2016 Maarten Van Puymbroeck
 */
public class SimpleTVGroup extends SimpleTV {


    public static final SimpleTVGroup instance() {
        return new SimpleTVGroup();
    }


    public static Entry entry(Dag dag, Slot slot) {
        return Entries.entryOf(DAY, dag, SLOT, slot);
    }

    // For test purposes
    public static final EntryData getEntryData(Dag dag, Slot slot) {
        return DefaultEntryData.of(TestMap.of(
                DAY.getName(), dag.toString(),
                SLOT.getName(), slot.toString()
        ));
    }

    static final InmemorySimpleGroup INMEMORY_TEST_GROUP = new InmemorySimpleGroup(NAME, DEFINITION, DAY.getName(), SLOT.getName());

    static final ElasticSearchSimpleGroup ELASTICSEARCH_TEST_GROUP = new ElasticSearchSimpleGroup(NAME, DEFINITION, DAY.getName(), SLOT.getName());

}
