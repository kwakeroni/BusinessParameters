package be.kwakeroni.scratch.tv;

import be.kwakeroni.parameters.basic.backend.es.ElasticSearchPostFilterRangedGroup;
import be.kwakeroni.parameters.basic.type.Ranges;
import be.kwakeroni.parameters.definition.api.ParameterGroupDefinition;
import be.kwakeroni.scratch.tv.definition.RangedFilterTV;

/**
 * (C) 2017 Maarten Van Puymbroeck
 */
public class RangedFilterTVGroup extends RangedFilterTV implements AbstractRangedTVGroup {

    public static final RangedFilterTVGroup instance() {
        return new RangedFilterTVGroup();
    }

    @Override
    public ParameterGroupDefinition getDefinition() {
        return DEFINITION;
    }

    static final ElasticSearchPostFilterRangedGroup ELASTICSEARCH_TEST_GROUP =
            new ElasticSearchPostFilterRangedGroup(SLOT.getName(), Ranges.stringRangeTypeOf(Slot.type), DEFINITION, AbstractRangedTVGroup.elasticSearchSubGroup(NAME, DEFINITION));

}
