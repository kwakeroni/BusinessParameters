package be.kwakeroni.scratch.tv;

import be.kwakeroni.parameters.backend.inmemory.api.EntryData;
import be.kwakeroni.parameters.backend.inmemory.api.GroupData;
import be.kwakeroni.parameters.backend.inmemory.support.DefaultEntryData;
import be.kwakeroni.parameters.basic.backend.es.ElasticSearchQueryBasedRangedGroup;
import be.kwakeroni.parameters.basic.backend.inmemory.InmemoryMappedGroup;
import be.kwakeroni.parameters.basic.backend.inmemory.InmemoryRangedGroup;
import be.kwakeroni.parameters.basic.backend.inmemory.InmemorySimpleGroup;
import be.kwakeroni.parameters.basic.client.model.Mapped;
import be.kwakeroni.parameters.basic.client.model.Ranged;
import be.kwakeroni.parameters.basic.client.model.Simple;
import be.kwakeroni.parameters.basic.client.query.MappedQuery;
import be.kwakeroni.parameters.basic.client.query.RangedQuery;
import be.kwakeroni.parameters.basic.client.query.ValueQuery;
import be.kwakeroni.parameters.basic.client.support.Entries;
import be.kwakeroni.parameters.basic.definition.builder.RangedDefinitionBuilder;
import be.kwakeroni.parameters.basic.type.Range;
import be.kwakeroni.parameters.basic.type.Ranges;
import be.kwakeroni.parameters.client.api.model.Entry;
import be.kwakeroni.parameters.client.api.model.Parameter;
import be.kwakeroni.parameters.client.api.model.ParameterGroup;
import be.kwakeroni.parameters.client.api.query.Query;
import be.kwakeroni.parameters.definition.api.ParameterGroupDefinition;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import static be.kwakeroni.parameters.basic.definition.BasicGroup.*;
import static be.kwakeroni.parameters.types.support.ParameterTypes.STRING;

/**
 * (C) 2017 Maarten Van Puymbroeck
 */
public abstract class AbstractMappedRangedTVGroup implements ParameterGroup<Mapped<Dag, Ranged<Slot, Simple>>>, ParameterGroupDefinition {


    public static Parameter<Dag> DAY = new DefaultParameter<>("day", Dag.type);
    public static Parameter<Range<Slot>> SLOT = new DefaultParameter<>("slot", Ranges.rangeTypeOf(Slot.type));
    public static Parameter<String> PROGRAM = new DefaultParameter<>("program", STRING);

    public static Entry entry(Dag day, Slot from, Slot to, String program) {
        return Entries.entryOf(DAY, day, SLOT, Range.of(from, to), PROGRAM, program);
    }


    // For test purposes
    public static EntryData entryData(Dag day, Slot from, Slot to, String program) {
        return DefaultEntryData.of(
                DAY.getName(), day.toString(),
                SLOT.getName(), Ranges.toRangeString(from, to, Slot::toString),
                PROGRAM.getName(), program
        );
    }

    public static Map<String, ?> entryData(Dag day, Slot from, Slot to, String program, boolean addRangeLimits) {
        if (addRangeLimits) {
            Map<String, Object> map = new HashMap<>(5);
            map.put(DAY.getName(), day.toString());
            map.put(SLOT.getName(), Ranges.toRangeString(from, to, Slot::toString));
            map.put(ElasticSearchQueryBasedRangedGroup.getFromParameter(SLOT.getName()), from.toInt());
            map.put(ElasticSearchQueryBasedRangedGroup.getToParameter(SLOT.getName()), to.toInt());
            map.put(PROGRAM.getName(), program);
            return map;
        } else {
            return entryData(day, from, to, program).asMap();
        }
    }

    // For test purposes
    public static final GroupData getData(String name, EntryData... data) {
        return new DefaultGroupData(inmemoryTestGroup(name), data);
    }

    // For test purposes
    public static Query<Mapped<Dag, Ranged<Slot, Simple>>, String> programQuery(Dag day, Slot slot) {
        return valueQuery(day, slot, AbstractMappedRangedTVGroup.PROGRAM);
    }

    public static <T> Query<Mapped<Dag, Ranged<Slot, Simple>>, T> valueQuery(Dag day, Slot slot, Parameter<T> parameter) {
        return new MappedQuery<>(day, Dag.type,
                new RangedQuery<>(slot, Slot.type,
                        new ValueQuery<>(parameter)));
    }


    public static final InmemoryMappedGroup inmemoryTestGroup(String name) {
        return new InmemoryMappedGroup(DAY.getName(),
                new InmemoryRangedGroup(SLOT.getName(), Ranges.stringRangeTypeOf(Slot.type),
                        new InmemorySimpleGroup(name, DAY.getName(), SLOT.getName(), PROGRAM.getName())));
    }

    protected static final ParameterGroupDefinition definition(String name, Function<RangedDefinitionBuilder, RangedDefinitionBuilder> withRangeParameter) {
        return mappedGroup()
                .withKeyParameter(DAY.getName())
                .mappingTo(withRangeParameter.apply(rangedGroup())
                        .mappingTo(group()
                                .withParameter(PROGRAM.getName())))
                .build(name);
    }


}
