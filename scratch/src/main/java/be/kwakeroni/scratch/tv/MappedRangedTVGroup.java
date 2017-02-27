package be.kwakeroni.scratch.tv;

import be.kwakeroni.parameters.basic.backend.es.ElasticSearchMappedGroup;
import be.kwakeroni.parameters.basic.backend.es.ElasticSearchPostFilterRangedGroup;
import be.kwakeroni.parameters.basic.backend.es.ElasticSearchSimpleGroup;
import be.kwakeroni.parameters.basic.client.query.MappedQuery;
import be.kwakeroni.parameters.basic.client.query.RangedQuery;
import be.kwakeroni.parameters.basic.client.query.ValueQuery;
import be.kwakeroni.parameters.basic.client.support.Entries;
import be.kwakeroni.parameters.basic.type.Range;
import be.kwakeroni.parameters.basic.type.Ranges;
import be.kwakeroni.parameters.client.api.model.Entry;
import be.kwakeroni.parameters.client.api.model.Parameter;
import be.kwakeroni.parameters.client.api.model.ParameterGroup;
import be.kwakeroni.parameters.basic.backend.inmemory.InmemoryMappedGroup;
import be.kwakeroni.parameters.basic.backend.inmemory.InmemoryRangedGroup;
import be.kwakeroni.parameters.basic.backend.inmemory.InmemorySimpleGroup;
import be.kwakeroni.parameters.backend.inmemory.api.EntryData;
import be.kwakeroni.parameters.backend.inmemory.api.GroupData;
import be.kwakeroni.parameters.backend.inmemory.support.DefaultEntryData;
import be.kwakeroni.parameters.basic.client.model.Mapped;
import be.kwakeroni.parameters.basic.client.model.Ranged;
import be.kwakeroni.parameters.basic.client.model.Simple;
import be.kwakeroni.parameters.client.api.query.Query;

import static be.kwakeroni.parameters.types.support.ParameterTypes.*;

/**
 * (C) 2017 Maarten Van Puymbroeck
 */
public class MappedRangedTVGroup implements ParameterGroup<Mapped<Dag, Ranged<Slot, Simple>>> {


    public static final MappedRangedTVGroup instance() { return new MappedRangedTVGroup(); }

    @Override
    public String getName() {
        return NAME;
    }
    public static Parameter<Dag> DAY = new DefaultParameter<>("day", Dag.type);
    public static Parameter<Range<Slot>> SLOT = new DefaultParameter<>("slot", Ranges.rangeTypeOf(Slot.type));
    public static Parameter<String> PROGRAM = new DefaultParameter<>("program", STRING);

    public static Entry entry(Dag day, Slot from, Slot to, String program) {
        return Entries.entryOf(DAY, day, SLOT, Range.of(from, to), PROGRAM, program);
    }


    // For test purposes
    public static EntryData entryData(Dag day, Slot from, Slot to, String program){
        return DefaultEntryData.of(
                DAY.getName(), day.toString(),
                SLOT.getName(), Ranges.toRangeString(from, to, Slot::toString),
                PROGRAM.getName(), program
        );
    }

    // For test purposes
    public static final GroupData getData(EntryData... data){
                return new DefaultGroupData(INMEMORY_GROUP, data);
    }

    // For test purposes
    public static Query<Mapped<Dag, Ranged<Slot, Simple>>, String> programQuery(Dag day, Slot slot){
                return valueQuery(day, slot, MappedRangedTVGroup.PROGRAM);
    }

    public static <T> Query<Mapped<Dag, Ranged<Slot, Simple>>, T> valueQuery(Dag day, Slot slot, Parameter<T> parameter){
        return new MappedQuery<>(day, Dag.type,
                new RangedQuery<>(slot, Slot.type,
                        new ValueQuery<>(parameter)));
    }



    private static final String NAME = "tv.mapped-ranged";
    private static final InmemoryMappedGroup INMEMORY_GROUP = new InmemoryMappedGroup(DAY.getName(),
            new InmemoryRangedGroup(SLOT.getName(), Ranges.stringRangeTypeOf(Slot.type),
                    new InmemorySimpleGroup(NAME, DAY.getName(), SLOT.getName(), PROGRAM.getName())));
    public static final ElasticSearchMappedGroup ELASTICSEARCH_POSTFILTER_GROUP = new ElasticSearchMappedGroup(DAY.getName(),
            new ElasticSearchPostFilterRangedGroup(SLOT.getName(), Ranges.stringRangeTypeOf(Slot.type),
                    new ElasticSearchSimpleGroup(NAME, DAY.getName(), SLOT.getName(), PROGRAM.getName())));



}
