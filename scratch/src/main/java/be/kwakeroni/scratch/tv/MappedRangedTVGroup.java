package be.kwakeroni.scratch.tv;

import be.kwakeroni.parameters.backend.es.api.ElasticSearchDataType;
import be.kwakeroni.parameters.backend.es.api.ElasticSearchGroup;
import be.kwakeroni.parameters.backend.inmemory.api.EntryData;
import be.kwakeroni.parameters.backend.inmemory.api.GroupData;
import be.kwakeroni.parameters.backend.inmemory.support.DefaultEntryData;
import be.kwakeroni.parameters.basic.backend.es.ElasticSearchMappedGroup;
import be.kwakeroni.parameters.basic.backend.es.ElasticSearchPostFilterRangedGroup;
import be.kwakeroni.parameters.basic.backend.es.ElasticSearchQueryBasedRangedGroup;
import be.kwakeroni.parameters.basic.backend.es.ElasticSearchSimpleGroup;
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
import be.kwakeroni.parameters.basic.definition.BasicGroupBuilder;
import be.kwakeroni.parameters.basic.definition.RangedGroupBuilder;
import be.kwakeroni.parameters.basic.type.Range;
import be.kwakeroni.parameters.basic.type.Ranges;
import be.kwakeroni.parameters.client.api.model.Entry;
import be.kwakeroni.parameters.client.api.model.Parameter;
import be.kwakeroni.parameters.client.api.model.ParameterGroup;
import be.kwakeroni.parameters.client.api.query.Query;
import be.kwakeroni.parameters.definition.api.GroupBuilderFactoryContext;
import be.kwakeroni.parameters.definition.api.ParameterGroupDefinition;

import java.util.HashMap;
import java.util.Map;

import static be.kwakeroni.parameters.types.support.ParameterTypes.STRING;

/**
 * (C) 2017 Maarten Van Puymbroeck
 */
public class MappedRangedTVGroup implements ParameterGroup<Mapped<Dag, Ranged<Slot, Simple>>>, ParameterGroupDefinition {

    private final boolean withRangeLimits;

    @Override
    public String getName() {
        return NAME;
    }

    public static Parameter<Dag> DAY = new DefaultParameter<>("day", Dag.type);
    public static Parameter<Range<Slot>> SLOT = new DefaultParameter<>("slot", Ranges.rangeTypeOf(Slot.type));
    public static Parameter<String> PROGRAM = new DefaultParameter<>("program", STRING);

    public static final MappedRangedTVGroup instance() {
        return new MappedRangedTVGroup(false);
    }

    public static final MappedRangedTVGroup withRangeLimits() {
        return new MappedRangedTVGroup(true);
    }

    public static final MappedRangedTVGroup withoutRangeLimits() {
        return new MappedRangedTVGroup(false);
    }

    private MappedRangedTVGroup(boolean withRangeLimits) {
        this.withRangeLimits = withRangeLimits;
    }

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
    public static final GroupData getData(EntryData... data) {
        return new DefaultGroupData(INMEMORY_GROUP, data);
    }

    // For test purposes
    public static Query<Mapped<Dag, Ranged<Slot, Simple>>, String> programQuery(Dag day, Slot slot) {
        return valueQuery(day, slot, MappedRangedTVGroup.PROGRAM);
    }

    public static <T> Query<Mapped<Dag, Ranged<Slot, Simple>>, T> valueQuery(Dag day, Slot slot, Parameter<T> parameter) {
        return new MappedQuery<>(day, Dag.type,
                new RangedQuery<>(slot, Slot.type,
                        new ValueQuery<>(parameter)));
    }


    private static final String NAME = "tv.mapped-ranged";
    public static final InmemoryMappedGroup INMEMORY_GROUP = new InmemoryMappedGroup(DAY.getName(),
            new InmemoryRangedGroup(SLOT.getName(), Ranges.stringRangeTypeOf(Slot.type),
                    new InmemorySimpleGroup(NAME, DAY.getName(), SLOT.getName(), PROGRAM.getName())));
    private static final ElasticSearchMappedGroup ELASTICSEARCH_GROUP_WITH_POSTFILTER = new ElasticSearchMappedGroup(DAY.getName(),
            new ElasticSearchPostFilterRangedGroup(SLOT.getName(), Ranges.stringRangeTypeOf(Slot.type),
                    new ElasticSearchSimpleGroup(NAME, DAY.getName(), SLOT.getName(), PROGRAM.getName())));
    private static final ElasticSearchMappedGroup ELASTICSEARCH_GROUP_WITH_QUERY = new ElasticSearchMappedGroup(DAY.getName(),
            new ElasticSearchQueryBasedRangedGroup(SLOT.getName(),
                    ElasticSearchDataType.INTEGER, string -> Slot.fromString(string).toInt(), new ElasticSearchSimpleGroup(NAME, DAY.getName(), SLOT.getName(), PROGRAM.getName())));

    public static ElasticSearchGroup elasticSearchGroup(boolean withRangeLimits) {
        return (withRangeLimits) ? ELASTICSEARCH_GROUP_WITH_QUERY : ELASTICSEARCH_GROUP_WITH_POSTFILTER;
    }

    @Override
    public <G> G createGroup(GroupBuilderFactoryContext<G> context) {
        BasicGroupBuilder<G> builder = BasicGroupBuilder.from(context);
        return builder.mapped()
                .withKeyParameter(DAY.getName())
                .mappingTo(ranged(builder)
                        .mappingTo(builder.group(NAME)
                                .withParameter(DAY.getName())
                                .withParameter(SLOT.getName())
                                .withParameter(PROGRAM.getName())))
                .build();
    }

    private <G> RangedGroupBuilder<G> ranged(BasicGroupBuilder<G> builder) {
        if (withRangeLimits) {
            return builder.ranged().withRangeParameter(SLOT.getName(), Slot.type);
        } else {
            return builder.ranged().withComparableRangeParameter(SLOT.getName(), Slot.type);
        }
    }

}
