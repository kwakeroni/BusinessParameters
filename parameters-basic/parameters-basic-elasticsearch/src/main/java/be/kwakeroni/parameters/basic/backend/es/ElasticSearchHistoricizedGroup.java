package be.kwakeroni.parameters.basic.backend.es;

import be.kwakeroni.parameters.backend.es.api.ElasticSearchDataType;
import be.kwakeroni.parameters.backend.es.api.ElasticSearchGroup;
import be.kwakeroni.parameters.definition.api.ParameterGroupDefinition;
import be.kwakeroni.parameters.types.support.ParameterTypes;

public class ElasticSearchHistoricizedGroup extends ElasticSearchQueryBasedRangedGroup {

    public ElasticSearchHistoricizedGroup(String periodParameterName, ParameterGroupDefinition definition, ElasticSearchGroup subGroup) {
        super(periodParameterName,
                ElasticSearchDataType.LOCAL_DATE,
                ParameterTypes.LOCAL_DATE::fromString,
                definition,
                subGroup);
    }
}
