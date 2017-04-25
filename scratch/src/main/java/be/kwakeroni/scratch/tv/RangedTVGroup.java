package be.kwakeroni.scratch.tv;

import be.kwakeroni.parameters.backend.es.api.ElasticSearchDataType;
import be.kwakeroni.parameters.backend.es.api.ElasticSearchGroup;
import be.kwakeroni.parameters.backend.inmemory.api.EntryData;
import be.kwakeroni.parameters.backend.inmemory.api.GroupData;
import be.kwakeroni.parameters.backend.inmemory.support.DefaultEntryData;
import be.kwakeroni.parameters.basic.backend.es.ElasticSearchPostFilterRangedGroup;
import be.kwakeroni.parameters.basic.backend.es.ElasticSearchQueryBasedRangedGroup;
import be.kwakeroni.parameters.basic.backend.es.ElasticSearchSimpleGroup;
import be.kwakeroni.parameters.basic.backend.inmemory.InmemoryRangedGroup;
import be.kwakeroni.parameters.basic.backend.inmemory.InmemorySimpleGroup;
import be.kwakeroni.parameters.basic.client.model.Ranged;
import be.kwakeroni.parameters.basic.client.model.Simple;
import be.kwakeroni.parameters.basic.client.query.RangedQuery;
import be.kwakeroni.parameters.basic.client.query.ValueQuery;
import be.kwakeroni.parameters.basic.client.support.Entries;
import be.kwakeroni.parameters.basic.definition.BasicGroup;
import be.kwakeroni.parameters.basic.definition.RangedDefinitionBuilder;
import be.kwakeroni.parameters.basic.type.Range;
import be.kwakeroni.parameters.basic.type.Ranges;
import be.kwakeroni.parameters.client.api.model.Entry;
import be.kwakeroni.parameters.client.api.model.Parameter;
import be.kwakeroni.parameters.client.api.model.ParameterGroup;
import be.kwakeroni.parameters.client.api.query.Query;
import be.kwakeroni.parameters.definition.api.ParameterGroupDefinition;
import be.kwakeroni.parameters.definition.api.factory.GroupFactoryContext;

import java.util.HashMap;
import java.util.Map;

import static be.kwakeroni.parameters.basic.definition.BasicGroup.group;
import static be.kwakeroni.parameters.types.support.ParameterTypes.STRING;

/**
 * (C) 2017 Maarten Van Puymbroeck
 */
public class RangedTVGroup implements ParameterGroup<Ranged<Slot, Simple>>, ParameterGroupDefinition {

    private final boolean withRangeLimits;

    @Override
    public String getName() {
        return NAME;
    }

    public static Parameter<Range<Slot>> SLOT = new DefaultParameter<>("slot", Ranges.rangeTypeOf(Slot.type));
    public static Parameter<String> PROGRAM = new DefaultParameter<>("program", STRING);

    public static final RangedTVGroup instance() {
        return new RangedTVGroup(false);
    }

    public static final RangedTVGroup withRangeLimits() {
        return new RangedTVGroup(true);
    }

    public static final RangedTVGroup withoutRangeLimits() {
        return new RangedTVGroup(false);
    }

    public RangedTVGroup(boolean withRangeLimits) {
        this.withRangeLimits = withRangeLimits;
    }

    public static Entry entry(Slot from, Slot to, String program) {
        return Entries.entryOf(SLOT, Range.of(from, to), PROGRAM, program);
    }

    // For test purposes
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
    public static final GroupData getData(Slot slot0From, Slot slot0To, String program0, Slot slot1From, Slot slot1To, String program1) {
        return new DefaultGroupData(
                INMEMORY_GROUP,
                entryData(slot0From, slot0To, program0),
                entryData(slot1From, slot1To, program1)
        );
    }

    // For test purposes
    public static Query<Ranged<Slot, Simple>, String> programQuery(Slot slot) {
        return new RangedQuery<>(slot, Slot.type,
                new ValueQuery<>(MappedTVGroup.PROGRAM));
    }

    private static final String NAME = "tv.ranged";
    public static final InmemoryRangedGroup INMEMORY_GROUP = new InmemoryRangedGroup(SLOT.getName(), Ranges.stringRangeTypeOf(Slot.type), new InmemorySimpleGroup(NAME, SLOT.getName(), PROGRAM.getName()));
    private static final ElasticSearchGroup ELASTICSEARCH_SUBGROUP = new ElasticSearchSimpleGroup(NAME, SLOT.getName(), PROGRAM.getName());
    private static final ElasticSearchPostFilterRangedGroup ELASTICSEARCH_GROUP_WITH_POSTFILTER =
            new ElasticSearchPostFilterRangedGroup(SLOT.getName(), Ranges.stringRangeTypeOf(Slot.type), ELASTICSEARCH_SUBGROUP);
    private static final ElasticSearchQueryBasedRangedGroup ELASTICSEARCH_GROUP_WITH_QUERY =
            new ElasticSearchQueryBasedRangedGroup(SLOT.getName(),
                    ElasticSearchDataType.INTEGER,
                    string -> Slot.fromString(string).toInt(),
                    ELASTICSEARCH_SUBGROUP);

    public static ElasticSearchGroup elasticSearchGroup(boolean withRangeLimits) {
        return (withRangeLimits) ? ELASTICSEARCH_GROUP_WITH_QUERY : ELASTICSEARCH_GROUP_WITH_POSTFILTER;
    }


    @Override
    public <G> G createGroup(GroupFactoryContext<G> context) {
        return definition().createGroup(context);
    }

    private final ParameterGroupDefinition definition() {
        return rangedGroup()
                .mappingTo(group()
                        .withParameter(PROGRAM.getName()))
                .build(NAME);
    }

    private RangedDefinitionBuilder rangedGroup() {
        if (withRangeLimits) {
            return BasicGroup.rangedGroup().withRangeParameter(SLOT.getName(), Slot.type);
        } else {
            return BasicGroup.rangedGroup().withComparableRangeParameter(SLOT.getName(), Slot.type);
        }
    }

}
