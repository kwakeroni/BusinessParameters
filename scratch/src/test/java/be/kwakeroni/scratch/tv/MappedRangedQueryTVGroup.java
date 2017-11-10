package be.kwakeroni.scratch.tv;

import be.kwakeroni.parameters.backend.es.api.ElasticSearchDataType;
import be.kwakeroni.parameters.basic.backend.es.ElasticSearchMappedGroup;
import be.kwakeroni.parameters.basic.backend.es.ElasticSearchQueryBasedRangedGroup;
import be.kwakeroni.parameters.basic.backend.es.ElasticSearchSimpleGroup;
import be.kwakeroni.parameters.basic.backend.inmemory.InmemoryMappedGroup;
import be.kwakeroni.scratch.tv.definition.MappedRangedQueryTV;

/**
 * Created by kwakeroni on 27/04/17.
 */
public class MappedRangedQueryTVGroup extends MappedRangedQueryTV implements AbstractMappedRangedTVGroup {

    public static final AbstractMappedRangedTVGroup instance() {
        return new MappedRangedQueryTVGroup();
    }

    static final InmemoryMappedGroup INMEMORY_TEST_GROUP = AbstractMappedRangedTVGroup.inmemoryTestGroup(NAME, DEFINITION);

    static final ElasticSearchMappedGroup ELASTICSEARCH_TEST_GROUP = new ElasticSearchMappedGroup(DAY.getName(), DEFINITION,
            new ElasticSearchQueryBasedRangedGroup(SLOT.getName(),
                    ElasticSearchDataType.INTEGER, string -> Slot.fromString(string).toInt(), DEFINITION,
                    new ElasticSearchSimpleGroup(NAME, DEFINITION, DAY.getName(), SLOT.getName(), PROGRAM.getName())));
}
