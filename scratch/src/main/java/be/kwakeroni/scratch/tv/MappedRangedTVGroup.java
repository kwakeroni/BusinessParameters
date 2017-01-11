package be.kwakeroni.scratch.tv;

import be.kwakeroni.parameters.backend.api.BackendGroup;
import be.kwakeroni.parameters.backend.inmemory.api.InMemoryQuery;
import be.kwakeroni.parameters.client.api.model.Parameter;
import be.kwakeroni.parameters.client.api.model.ParameterGroup;
import be.kwakeroni.parameters.basic.backend.inmemory.InmemoryMappedGroup;
import be.kwakeroni.parameters.basic.backend.inmemory.InmemoryRangedGroup;
import be.kwakeroni.parameters.basic.backend.inmemory.InmemorySimpleGroup;
import be.kwakeroni.parameters.backend.inmemory.api.EntryData;
import be.kwakeroni.parameters.backend.inmemory.api.GroupData;
import be.kwakeroni.parameters.backend.inmemory.support.DefaultEntryData;
import be.kwakeroni.parameters.basic.client.model.Mapped;
import be.kwakeroni.parameters.basic.client.model.Range;
import be.kwakeroni.parameters.basic.client.model.Ranged;
import be.kwakeroni.parameters.basic.client.model.Simple;
import be.kwakeroni.parameters.basic.client.support.Ranges;

import java.util.stream.Stream;

import static java.util.function.Function.identity;

/**
 * (C) 2017 Maarten Van Puymbroeck
 */
public class MappedRangedTVGroup implements ParameterGroup<Mapped<Dag, Ranged<Slot, Simple>>> {

    public static final MappedRangedTVGroup instance() { return new MappedRangedTVGroup(); }

    @Override
    public String getName() {
        return "tv.mapped-ranged";
    }

    public static Parameter<Dag> DAY = new DefaultParameter<>("day", Dag::valueOf, Dag::name);
    public static Parameter<Range<Slot>> SLOT = new DefaultParameter<>("slot", Ranges.fromStringOf(Slot::fromString), Ranges.toStringOf(Slot::toString));
    public static Parameter<String> PROGRAM = new DefaultParameter<>("program", identity(), identity());

    // For test purposes
    public static EntryData entry(Dag day, Slot from, Slot to, String program){
        return DefaultEntryData.of(
                DAY.getName(), day.toString(),
                SLOT.getName(), Ranges.toRangeString(from, to, Slot::toString),
                PROGRAM.getName(), program
        );
    }

    // For test purposes
    public static final GroupData getData(EntryData... data){
        InmemoryMappedGroup group =
                new InmemoryMappedGroup(DAY.getName(), String::equals,
                    new InmemoryRangedGroup(SLOT.getName(), Ranges.containsValueOf(Slot::fromString),
                        new InmemorySimpleGroup()));
        return new GroupData() {
            @Override
            public Stream<EntryData> getEntries() {
                return Stream.of(data);
            }

            @Override
            public BackendGroup<InMemoryQuery<?>> getGroup() {
                return group;
            }
        };
    }
}
