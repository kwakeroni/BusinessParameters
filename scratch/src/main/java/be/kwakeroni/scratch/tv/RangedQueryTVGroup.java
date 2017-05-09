package be.kwakeroni.scratch.tv;

import be.kwakeroni.parameters.backend.es.api.ElasticSearchDataType;
import be.kwakeroni.parameters.basic.backend.es.ElasticSearchQueryBasedRangedGroup;
import be.kwakeroni.parameters.basic.backend.inmemory.InmemoryRangedGroup;
import be.kwakeroni.parameters.definition.api.ParameterGroupDefinition;
import be.kwakeroni.parameters.definition.api.DefinitionVisitorContext;

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
    protected ParameterGroupDefinition getDefinition() {
        return DEFINITION;
    }

    public static final ParameterGroupDefinition DEFINITION = definition(NAME, rangedGroup -> rangedGroup.withRangeParameter(SLOT.getName(), Slot.type));

    static final InmemoryRangedGroup INMEMORY_TEST_GROUP = inmemoryTestGroup(NAME, DEFINITION);

    static final ElasticSearchQueryBasedRangedGroup ELASTICSEARCH_TEST_GROUP =
            new ElasticSearchQueryBasedRangedGroup(SLOT.getName(),
                    ElasticSearchDataType.INTEGER,
                    string -> Slot.fromString(string).toInt(),
                    DEFINITION,
                    elasticSearchSubGroup(NAME, DEFINITION));

}
