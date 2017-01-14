package be.kwakeroni.scratch.tv;

import be.kwakeroni.parameters.backend.inmemory.api.EntryData;
import be.kwakeroni.parameters.backend.inmemory.api.GroupData;
import be.kwakeroni.parameters.backend.inmemory.support.DefaultEntryData;
import be.kwakeroni.parameters.basic.backend.inmemory.InmemoryRangedGroup;
import be.kwakeroni.parameters.basic.backend.inmemory.InmemorySimpleGroup;
import be.kwakeroni.parameters.basic.client.model.Entry;
import be.kwakeroni.parameters.basic.client.model.Range;
import be.kwakeroni.parameters.basic.client.model.Ranged;
import be.kwakeroni.parameters.basic.client.model.Simple;
import be.kwakeroni.parameters.basic.client.support.Entries;
import be.kwakeroni.parameters.basic.client.support.Ranges;
import be.kwakeroni.parameters.client.api.model.Parameter;
import be.kwakeroni.parameters.client.api.model.ParameterGroup;

import static be.kwakeroni.parameters.types.support.ParameterTypes.STRING;

/**
 * (C) 2017 Maarten Van Puymbroeck
 */
public class RangedTVGroup implements ParameterGroup<Ranged<Slot, Simple>> {

    public static final RangedTVGroup instance() {
        return new RangedTVGroup();
    }

    @Override
    public String getName() {
        return "tv.ranged";
    }

    public static Parameter<Range<Slot>> SLOT = new DefaultParameter<>("slot", Ranges.rangeTypeOf(Slot.type));
    public static Parameter<String> PROGRAM = new DefaultParameter<>("program", STRING);

    public static Entry entry(Slot from, Slot to, String program) {
        return Entries.entryOf(SLOT, Range.of(from, to), PROGRAM, program);
    }

    // For test purposes
    private static EntryData entryData(Slot from, Slot to, String program) {
        return DefaultEntryData.of(
                SLOT.getName(), Ranges.toRangeString(from, to, Slot::toString),
                PROGRAM.getName(), program
        );
    }

    // For test purposes
    public static final GroupData getData(Slot slot0From, Slot slot0To, String program0, Slot slot1From, Slot slot1To, String program1) {
        return new DefaultGroupData(
                new InmemoryRangedGroup(SLOT.getName(), Ranges.containsValueOf(Slot::fromString), new InmemorySimpleGroup()),
                entryData(slot0From, slot0To, program0),
                entryData(slot1From, slot1To, program1)
        );
    }

}
