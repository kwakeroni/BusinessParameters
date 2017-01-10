package be.kwakeroni.scratch.tv;

import be.kwakeroni.parameters.api.backend.BackendGroup;
import be.kwakeroni.parameters.api.client.model.Parameter;
import be.kwakeroni.parameters.api.client.model.ParameterGroup;
import be.kwakeroni.parameters.backend.inmemory.InmemoryRangedGroup;
import be.kwakeroni.parameters.backend.inmemory.InmemorySimpleGroup;
import be.kwakeroni.parameters.backend.inmemory.api.DataQuery;
import be.kwakeroni.parameters.backend.inmemory.api.EntryData;
import be.kwakeroni.parameters.backend.inmemory.api.GroupData;
import be.kwakeroni.parameters.backend.inmemory.support.DefaultEntryData;
import be.kwakeroni.parameters.basic.client.model.Range;
import be.kwakeroni.parameters.basic.client.model.Ranged;
import be.kwakeroni.parameters.basic.client.model.Simple;
import be.kwakeroni.parameters.basic.client.support.Ranges;

import java.util.stream.Stream;

import static java.util.function.Function.identity;

/**
 * (C) 2017 Maarten Van Puymbroeck
 */
public class RangedTVGroup implements ParameterGroup<Ranged<Slot, Simple>> {

    public static final RangedTVGroup instance() { return new RangedTVGroup(); }

    @Override
    public String getName() {
        return "tv.ranged";
    }

    public static Parameter<Range<Slot>> SLOT = new DefaultParameter<>("slot", Ranges.fromStringOf(Slot::fromString), Ranges.toStringOf(Slot::toString));
    public static Parameter<String> PROGRAM = new DefaultParameter<>("program", identity(), identity());

    // For test purposes
    private static EntryData entryData(Slot from, Slot to, String program){
        return DefaultEntryData.of(
                SLOT.getName(), Ranges.toRangeString(from, to, Slot::toString),
                PROGRAM.getName(), program
        );
    }

    // For test purposes
    public static final GroupData getData(Slot slot0From, Slot slot0To, String program0, Slot slot1From, Slot slot1To, String program1){
        InmemoryRangedGroup group = new InmemoryRangedGroup(SLOT.getName(), Ranges.containsValueOf(Slot::fromString), new InmemorySimpleGroup());
        return new GroupData() {
            @Override
            public Stream<EntryData> getEntries() {
                return Stream.of(
                        entryData(slot0From, slot0To, program0),
                        entryData(slot1From, slot1To, program1)
                );
            }

            @Override
            public BackendGroup<DataQuery<?>> getGroup() {
                return group;
            }
        };
    }

}
