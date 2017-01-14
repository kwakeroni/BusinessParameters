package be.kwakeroni.scratch.tv;

import be.kwakeroni.parameters.backend.inmemory.api.EntryData;
import be.kwakeroni.parameters.backend.inmemory.api.GroupData;
import be.kwakeroni.parameters.backend.inmemory.support.DefaultEntryData;
import be.kwakeroni.parameters.basic.backend.inmemory.InmemoryMappedGroup;
import be.kwakeroni.parameters.basic.backend.inmemory.InmemorySimpleGroup;
import be.kwakeroni.parameters.basic.client.model.Entry;
import be.kwakeroni.parameters.basic.client.model.Mapped;
import be.kwakeroni.parameters.basic.client.model.Simple;
import be.kwakeroni.parameters.basic.client.support.Entries;
import be.kwakeroni.parameters.client.api.model.Parameter;
import be.kwakeroni.parameters.client.api.model.ParameterGroup;

import static be.kwakeroni.parameters.types.support.ParameterTypes.STRING;

/**
 * (C) 2017 Maarten Van Puymbroeck
 */
public class MappedTVGroup implements ParameterGroup<Mapped<Dag, Simple>> {

    public static final MappedTVGroup instance() {
        return new MappedTVGroup();
    }

    @Override
    public String getName() {
        return "tv.mapped";
    }

    public static Parameter<Dag> DAY = new DefaultParameter<>("day", Dag.type);
    public static Parameter<String> PROGRAM = new DefaultParameter<>("program", STRING);

    public static Entry entry(Dag dag, String program) {
        return Entries.entryOf(DAY, dag, PROGRAM, program);
    }

    // For test purposes
    private static EntryData entryData(Dag day, String program) {
        return DefaultEntryData.of(
                DAY.getName(), day.toString(),
                PROGRAM.getName(), program
        );
    }

    // For test purposes
    public static final GroupData getData(Dag dag0, String program0, Dag dag1, String program1) {
        return new DefaultGroupData(
                new InmemoryMappedGroup(DAY.getName(), String::equals, new InmemorySimpleGroup()),
                entryData(dag0, program0),
                entryData(dag1, program1)
        );
    }

}
