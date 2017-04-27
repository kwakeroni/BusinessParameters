package be.kwakeroni.scratch.tv;

import be.kwakeroni.parameters.backend.es.api.ElasticSearchDataType;
import be.kwakeroni.parameters.basic.backend.es.ElasticSearchMappedGroup;
import be.kwakeroni.parameters.basic.backend.es.ElasticSearchQueryBasedRangedGroup;
import be.kwakeroni.parameters.basic.backend.es.ElasticSearchSimpleGroup;
import be.kwakeroni.parameters.basic.backend.inmemory.InmemoryMappedGroup;
import be.kwakeroni.parameters.definition.api.ParameterGroupDefinition;
import be.kwakeroni.parameters.definition.api.factory.GroupFactoryContext;

/**
 * Created by kwakeroni on 27/04/17.
 */
public class MappedRangedQueryTVGroup extends AbstractMappedRangedTVGroup {

    public static final String NAME = "tv.mapped-ranged.query";

    public static final AbstractMappedRangedTVGroup instance() {
        return new MappedRangedQueryTVGroup();
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public <G> G createGroup(GroupFactoryContext<G> context) {
        return DEFINITION.createGroup(context);
    }

    static final InmemoryMappedGroup INMEMORY_TEST_GROUP = inmemoryTestGroup(NAME);

    static final ElasticSearchMappedGroup ELASTICSEARCH_TEST_GROUP = new ElasticSearchMappedGroup(DAY.getName(),
            new ElasticSearchQueryBasedRangedGroup(SLOT.getName(),
                    ElasticSearchDataType.INTEGER, string -> Slot.fromString(string).toInt(), new ElasticSearchSimpleGroup(NAME, DAY.getName(), SLOT.getName(), PROGRAM.getName())));

    public static final ParameterGroupDefinition DEFINITION = definition(NAME, rangedGroup -> rangedGroup.withRangeParameter(SLOT.getName(), Slot.type));
}
