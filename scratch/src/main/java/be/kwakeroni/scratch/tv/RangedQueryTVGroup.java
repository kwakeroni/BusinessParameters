package be.kwakeroni.scratch.tv;

import be.kwakeroni.parameters.backend.es.api.ElasticSearchDataType;
import be.kwakeroni.parameters.basic.backend.es.ElasticSearchQueryBasedRangedGroup;
import be.kwakeroni.parameters.basic.backend.inmemory.InmemoryRangedGroup;
import be.kwakeroni.parameters.definition.api.ParameterGroupDefinition;
import be.kwakeroni.parameters.definition.api.factory.GroupFactoryContext;

/**
 * (C) 2017 Maarten Van Puymbroeck
 */
public class RangedQueryTVGroup extends AbstractRangedTVGroup {

    public static final String NAME = "tv.ranged.query";

    public static final RangedQueryTVGroup instance() {
        return new RangedQueryTVGroup();
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public <G> G createGroup(GroupFactoryContext<G> context) {
        return DEFINITION.createGroup(context);
    }

    static final InmemoryRangedGroup INMEMORY_TEST_GROUP = inmemoryTestGroup(NAME);

    static final ElasticSearchQueryBasedRangedGroup ELASTICSEARCH_TEST_GROUP =
            new ElasticSearchQueryBasedRangedGroup(SLOT.getName(),
                    ElasticSearchDataType.INTEGER,
                    string -> Slot.fromString(string).toInt(),
                    elasticSearchSubGroup(NAME));

    public static final ParameterGroupDefinition DEFINITION = definition(NAME, rangedGroup -> rangedGroup.withRangeParameter(SLOT.getName(), Slot.type));

}
