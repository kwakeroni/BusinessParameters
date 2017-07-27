package be.kwakeroni.scratch.tv.definition;

import be.kwakeroni.parameters.basic.client.model.Simple;
import be.kwakeroni.parameters.client.api.model.Parameter;
import be.kwakeroni.parameters.client.api.model.ParameterGroup;
import be.kwakeroni.parameters.definition.api.ParameterGroupDefinition;
import be.kwakeroni.scratch.tv.Dag;
import be.kwakeroni.scratch.tv.DefaultParameter;
import be.kwakeroni.scratch.tv.Slot;

import static be.kwakeroni.parameters.basic.definition.BasicGroup.group;

/**
 * Created by kwakeroni on 25/07/17.
 */
public class SimpleTV implements ParameterGroup<Simple> {

    public static Parameter<Dag> DAY = new DefaultParameter<>("day", Dag.type);

    public static Parameter<Slot> SLOT = new DefaultParameter<>("slot", Slot.type);
    public static final String NAME = "tv.simple";

    public static final ParameterGroupDefinition DEFINITION =
            group()
                    .withParameter(DAY.getName())
                    .withParameter(SLOT.getName())
                    .build(NAME);

    @Override
    public String getName() {
        return NAME;
    }

}
