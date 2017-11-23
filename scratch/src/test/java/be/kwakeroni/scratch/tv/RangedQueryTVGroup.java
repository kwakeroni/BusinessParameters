package be.kwakeroni.scratch.tv;

import be.kwakeroni.parameters.backend.es.api.ElasticSearchDataType;
import be.kwakeroni.parameters.basic.backend.es.ElasticSearchQueryBasedRangedGroup;
import be.kwakeroni.parameters.basic.backend.inmemory.InmemoryRangedGroup;
import be.kwakeroni.parameters.basic.client.model.Ranged;
import be.kwakeroni.parameters.basic.client.model.Simple;
import be.kwakeroni.parameters.definition.api.ParameterGroupDefinition;
import be.kwakeroni.scratch.tv.definition.RangedQueryTV;

/**
 * (C) 2017 Maarten Van Puymbroeck
 */
public class RangedQueryTVGroup extends RangedQueryTV implements AbstractRangedTVGroup {

    public static final RangedQueryTVGroup instance() {
        return new RangedQueryTVGroup();
    }

    @Override
    public ParameterGroupDefinition<Ranged<Slot, Simple>> getDefinition() {
        return DEFINITION;
    }

    static final InmemoryRangedGroup INMEMORY_TEST_GROUP = AbstractRangedTVGroup.inmemoryTestGroup(NAME, DEFINITION);

    static final ElasticSearchQueryBasedRangedGroup ELASTICSEARCH_TEST_GROUP =
            new ElasticSearchQueryBasedRangedGroup(SLOT.getName(),
                    ElasticSearchDataType.INTEGER,
                    string -> Slot.fromString(string).toInt(),
                    DEFINITION,
                    AbstractRangedTVGroup.elasticSearchSubGroup(NAME, DEFINITION));

}
