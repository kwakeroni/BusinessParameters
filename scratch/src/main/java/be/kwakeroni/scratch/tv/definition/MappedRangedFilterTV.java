package be.kwakeroni.scratch.tv.definition;

import be.kwakeroni.parameters.basic.client.model.Mapped;
import be.kwakeroni.parameters.basic.client.model.Ranged;
import be.kwakeroni.parameters.basic.client.model.Simple;
import be.kwakeroni.parameters.definition.api.ParameterGroupDefinition;
import be.kwakeroni.scratch.tv.Dag;
import be.kwakeroni.scratch.tv.Slot;

/**
 * Created by kwakeroni on 25/07/17.
 */
public class MappedRangedFilterTV implements AbstractMappedRangedTV {

    public static final String NAME = "tv.mapped-ranged.filter";

    public static final ParameterGroupDefinition<Mapped<Dag, Ranged<Slot, Simple>>> DEFINITION =
            AbstractMappedRangedTV.definition(NAME, rangedGroup -> rangedGroup.withComparableRangeParameter(SLOT.getName(), Slot.type));

    @Override
    public String getName() {
        return NAME;
    }

}
