package be.kwakeroni.scratch.tv;

import be.kwakeroni.parameters.basic.backend.es.ElasticSearchMappedGroup;
import be.kwakeroni.parameters.basic.backend.es.ElasticSearchPostFilterRangedGroup;
import be.kwakeroni.parameters.basic.backend.es.ElasticSearchSimpleGroup;
import be.kwakeroni.parameters.basic.backend.inmemory.InmemoryMappedGroup;
import be.kwakeroni.parameters.basic.type.Ranges;
import be.kwakeroni.parameters.definition.api.ParameterGroupDefinition;

/**
 * Created by kwakeroni on 27/04/17.
 */
public class MappedRangedFilterTVGroup extends AbstractMappedRangedTVGroup {
    public static final String NAME = "tv.mapped-ranged.filter";

    public static final AbstractMappedRangedTVGroup instance() {
        return new MappedRangedFilterTVGroup();
    }

    @Override
    public String getName() {
        return NAME;
    }

    public static final ParameterGroupDefinition DEFINITION = definition(NAME, rangedGroup -> rangedGroup.withComparableRangeParameter(SLOT.getName(), Slot.type));

    static final InmemoryMappedGroup INMEMORY_TEST_GROUP = inmemoryTestGroup(NAME, DEFINITION);

    static final ElasticSearchMappedGroup ELASTICSEARCH_TEST_GROUP = new ElasticSearchMappedGroup(DAY.getName(), DEFINITION,
            new ElasticSearchPostFilterRangedGroup(SLOT.getName(), Ranges.stringRangeTypeOf(Slot.type), DEFINITION,
                    new ElasticSearchSimpleGroup(NAME, DEFINITION, DAY.getName(), SLOT.getName(), PROGRAM.getName())));

}
