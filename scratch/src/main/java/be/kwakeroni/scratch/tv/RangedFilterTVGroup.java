package be.kwakeroni.scratch.tv;

import be.kwakeroni.parameters.basic.backend.es.ElasticSearchPostFilterRangedGroup;
import be.kwakeroni.parameters.basic.type.Ranges;
import be.kwakeroni.parameters.definition.api.ParameterGroupDefinition;
import be.kwakeroni.parameters.definition.api.DefinitionVisitorContext;

/**
 * (C) 2017 Maarten Van Puymbroeck
 */
public class RangedFilterTVGroup extends AbstractRangedTVGroup {

    public static final String NAME = "tv.ranged.filter";

    public static final RangedFilterTVGroup instance() {
        return new RangedFilterTVGroup();
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public <G> G apply(DefinitionVisitorContext<G> context) {
        return DEFINITION.apply(context);
    }

    static final ElasticSearchPostFilterRangedGroup ELASTICSEARCH_TEST_GROUP =
            new ElasticSearchPostFilterRangedGroup(SLOT.getName(), Ranges.stringRangeTypeOf(Slot.type), elasticSearchSubGroup(NAME));

    public static final ParameterGroupDefinition DEFINITION = definition(NAME, rangedGroup -> rangedGroup.withComparableRangeParameter(SLOT.getName(), Slot.type));

}
