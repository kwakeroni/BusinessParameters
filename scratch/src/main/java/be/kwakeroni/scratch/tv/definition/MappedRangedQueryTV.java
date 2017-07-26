package be.kwakeroni.scratch.tv.definition;

import be.kwakeroni.parameters.definition.api.ParameterGroupDefinition;
import be.kwakeroni.scratch.tv.Slot;

/**
 * Created by kwakeroni on 25/07/17.
 */
public class MappedRangedQueryTV implements AbstractMappedRangedTV {


    public static final String NAME = "tv.mapped-ranged.query";

    public static final ParameterGroupDefinition DEFINITION = AbstractMappedRangedTV.definition(NAME, rangedGroup -> rangedGroup.withRangeParameter(SLOT.getName(), Slot.type));

    @Override
    public String getName() {
        return NAME;
    }

}
