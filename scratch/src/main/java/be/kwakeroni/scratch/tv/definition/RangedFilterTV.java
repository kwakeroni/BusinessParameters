package be.kwakeroni.scratch.tv.definition;

import be.kwakeroni.parameters.basic.client.model.Ranged;
import be.kwakeroni.parameters.basic.client.model.Simple;
import be.kwakeroni.parameters.definition.api.ParameterGroupDefinition;
import be.kwakeroni.scratch.tv.Slot;

/**
 * Created by kwakeroni on 25/07/17.
 */
public class RangedFilterTV implements AbstractRangedTV {

    public static final String NAME = "tv.ranged.filter";

    public static final ParameterGroupDefinition<Ranged<Slot, Simple>> DEFINITION = AbstractRangedTV.definition(NAME, rangedGroup -> rangedGroup.withComparableRangeParameter(SLOT.getName(), Slot.type));

    @Override
    public String getName() {
        return NAME;
    }

}
