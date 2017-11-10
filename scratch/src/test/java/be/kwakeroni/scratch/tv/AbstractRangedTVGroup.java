package be.kwakeroni.scratch.tv;

import be.kwakeroni.parameters.backend.es.api.ElasticSearchGroup;
import be.kwakeroni.parameters.backend.inmemory.api.EntryData;
import be.kwakeroni.parameters.backend.inmemory.support.DefaultEntryData;
import be.kwakeroni.parameters.basic.backend.es.ElasticSearchQueryBasedRangedGroup;
import be.kwakeroni.parameters.basic.backend.es.ElasticSearchSimpleGroup;
import be.kwakeroni.parameters.basic.backend.inmemory.InmemoryRangedGroup;
import be.kwakeroni.parameters.basic.backend.inmemory.InmemorySimpleGroup;
import be.kwakeroni.parameters.basic.client.model.Ranged;
import be.kwakeroni.parameters.basic.client.model.Simple;
import be.kwakeroni.parameters.basic.client.query.RangedQuery;
import be.kwakeroni.parameters.basic.client.query.ValueQuery;
import be.kwakeroni.parameters.basic.client.support.Entries;
import be.kwakeroni.parameters.basic.type.Range;
import be.kwakeroni.parameters.basic.type.Ranges;
import be.kwakeroni.parameters.client.api.model.Entry;
import be.kwakeroni.parameters.client.api.query.Query;
import be.kwakeroni.parameters.definition.api.ParameterGroupDefinition;
import be.kwakeroni.scratch.tv.definition.AbstractRangedTV;

import java.util.HashMap;
import java.util.Map;

/**
 * (C) 2017 Maarten Van Puymbroeck
 */
public interface AbstractRangedTVGroup extends AbstractRangedTV {


    public static Entry entry(Slot from, Slot to, String program) {
        return Entries.entryOf(SLOT, Range.of(from, to), PROGRAM, program);
    }

    // For test purposes
    ParameterGroupDefinition getDefinition();

    public static EntryData entryData(Slot from, Slot to, String program) {
        return DefaultEntryData.of(
                SLOT.getName(), Ranges.toRangeString(from, to, Slot::toString),
                PROGRAM.getName(), program
        );

    }

    public static Map<String, ?> entryData(Slot from, Slot to, String program, boolean addRangeLimits) {
        if (addRangeLimits) {
            Map<String, Object> map = new HashMap<>(4);
            map.put(SLOT.getName(), Ranges.toRangeString(from, to, Slot::toString));
            map.put(ElasticSearchQueryBasedRangedGroup.getFromParameter(SLOT.getName()), from.toInt());
            map.put(ElasticSearchQueryBasedRangedGroup.getToParameter(SLOT.getName()), to.toInt());
            map.put(PROGRAM.getName(), program);
            return map;
        } else {
            return entryData(from, to, program).asMap();
        }
    }

    // For test purposes
    public static Query<Ranged<Slot, Simple>, String> programQuery(Slot slot) {
        return new RangedQuery<>(slot, Slot.type,
                new ValueQuery<>(MappedTVGroup.PROGRAM));
    }

    public static InmemoryRangedGroup inmemoryTestGroup(String name, ParameterGroupDefinition definition) {
        return new InmemoryRangedGroup(SLOT.getName(), Ranges.stringRangeTypeOf(Slot.type), definition, new InmemorySimpleGroup(name, definition, SLOT.getName(), PROGRAM.getName()));
    }

    public static ElasticSearchGroup elasticSearchSubGroup(String name, ParameterGroupDefinition definition) {
        return new ElasticSearchSimpleGroup(name, definition, SLOT.getName(), PROGRAM.getName());
    }


}
