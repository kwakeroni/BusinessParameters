package be.kwakeroni.scratch.tv;

import be.kwakeroni.parameters.backend.inmemory.api.DataQuery;
import be.kwakeroni.parameters.backend.inmemory.api.EntryData;
import be.kwakeroni.parameters.backend.inmemory.api.GroupData;
import be.kwakeroni.parameters.backend.inmemory.api.type.InMemoryRange;
import be.kwakeroni.parameters.backend.inmemory.basic.InMemoryMappedEntrySet;
import be.kwakeroni.parameters.backend.inmemory.basic.InMemoryRangedEntrySet;
import be.kwakeroni.parameters.backend.inmemory.basic.InMemorySimpleEntrySet;
import be.kwakeroni.parameters.backend.inmemory.support.DefaultEntryData;
import be.kwakeroni.parameters.client.connector.EntrySet;

import java.util.Collection;
import java.util.HashSet;
import java.util.stream.Stream;

import static be.kwakeroni.parameters.test.TVProgram.*;

/**
 * (C) 2016 Maarten Van Puymbroeck
 */
public class TVProgramGroupData implements GroupData {

    private final EntrySet<DataQuery<?>> entrySet;
    private final Collection<EntryData> entries;

    public TVProgramGroupData() {

        // Mapped<Dag, Ranged<Slot, Simple>>

//        entrySet = new InMemoryMappedEntrySet("dag", String::equals,
//                new InMemoryRangedEntrySet<>("slot", InMemoryRange.ofType(Slot.TYPE),
//                        new InMemorySimpleEntrySet()));

        this.entries = new HashSet<>();
    }

    @Override
    public EntrySet<DataQuery<?>> getEntrySet() {
        return entrySet;
    }

    @Override
    public Stream<EntryData> getEntries() {
        return entries.stream();
    }

    public void add(Dag dag, Slot slot, String name) {
        entries.add(DefaultEntryData.of(
                DAG.getName(), dag.toString(),
                SLOT.getName(), "[8.0,9.0]",
                NAME.getName(), name
        ));
    }
}
