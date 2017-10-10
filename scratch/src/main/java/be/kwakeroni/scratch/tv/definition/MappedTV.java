package be.kwakeroni.scratch.tv.definition;

import be.kwakeroni.parameters.basic.client.model.Mapped;
import be.kwakeroni.parameters.basic.client.model.Simple;
import be.kwakeroni.parameters.client.api.model.Parameter;
import be.kwakeroni.parameters.client.api.model.ParameterGroup;
import be.kwakeroni.parameters.definition.api.ParameterGroupDefinition;
import be.kwakeroni.scratch.tv.Dag;
import be.kwakeroni.scratch.tv.DefaultParameter;

import static be.kwakeroni.parameters.basic.definition.BasicGroup.group;
import static be.kwakeroni.parameters.basic.definition.BasicGroup.mappedGroup;
import static be.kwakeroni.parameters.types.support.ParameterTypes.STRING;

/**
 * Created by kwakeroni on 25/07/17.
 */
public class MappedTV implements ParameterGroup<Mapped<Dag, Simple>> {

    @Override
    public String getName() {
        return NAME;
    }

    public static final Parameter<Dag> DAY = new DefaultParameter<>("day", Dag.type);
    public static final Parameter<String> PROGRAM = new DefaultParameter<>("program", STRING);

    public static final String NAME = "tv.mapped";
    public static final ParameterGroupDefinition DEFINITION =
            mappedGroup()
                    .withKeyParameter(DAY.getName())
                    .mappingTo(group()
                            .withParameter(PROGRAM.getName()))
                    .build(NAME);

}
