package be.kwakeroni.scratch.tv.definition;

import be.kwakeroni.parameters.basic.client.model.Historicized;
import be.kwakeroni.parameters.basic.client.model.Simple;
import be.kwakeroni.parameters.basic.definition.BasicGroup;
import be.kwakeroni.parameters.basic.type.Range;
import be.kwakeroni.parameters.basic.type.Ranges;
import be.kwakeroni.parameters.client.api.model.Parameter;
import be.kwakeroni.parameters.client.api.model.ParameterGroup;
import be.kwakeroni.parameters.definition.api.ParameterGroupDefinition;
import be.kwakeroni.scratch.tv.DefaultParameter;

import java.time.LocalDate;

import static be.kwakeroni.parameters.types.support.ParameterTypes.LOCAL_DATE;
import static be.kwakeroni.parameters.types.support.ParameterTypes.STRING;

public class HistoricizedTV implements ParameterGroup<Historicized<Simple>> {

    public static final String NAME = "tv.historicized";

    @Override
    public String getName() {
        return NAME;
    }

    public static Parameter<Range<LocalDate>> PERIOD = new DefaultParameter<>("period", Ranges.rangeTypeOf(LOCAL_DATE));
    public static Parameter<String> PROGRAM = new DefaultParameter<>("program", STRING);

    public static final ParameterGroupDefinition<Historicized<Simple>> DEFINITION = BasicGroup.historicizedGroup()
            .withParameter("period")
            .mappingTo(BasicGroup.group().withParameter(PROGRAM.getName()))
            .build(NAME);

}
