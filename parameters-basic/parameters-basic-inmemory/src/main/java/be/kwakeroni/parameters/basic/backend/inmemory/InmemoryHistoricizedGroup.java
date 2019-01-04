package be.kwakeroni.parameters.basic.backend.inmemory;

import be.kwakeroni.parameters.backend.inmemory.api.InMemoryGroup;
import be.kwakeroni.parameters.basic.type.Ranges;
import be.kwakeroni.parameters.definition.api.ParameterGroupDefinition;
import be.kwakeroni.parameters.types.support.ParameterTypes;

public class InmemoryHistoricizedGroup extends InmemoryRangedGroup {

    public InmemoryHistoricizedGroup(String periodParameterName, ParameterGroupDefinition<?> definition, InMemoryGroup subGroup) {
        super(periodParameterName, Ranges.stringRangeTypeOf(ParameterTypes.LOCAL_DATE), definition, subGroup);
    }
}
