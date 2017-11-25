package be.kwakeroni.scratch.tv.definition;

import be.kwakeroni.parameters.basic.client.model.Mapped;
import be.kwakeroni.parameters.basic.client.model.Ranged;
import be.kwakeroni.parameters.basic.client.model.Simple;
import be.kwakeroni.parameters.basic.definition.builder.RangedDefinitionBuilder;
import be.kwakeroni.parameters.basic.type.Range;
import be.kwakeroni.parameters.basic.type.Ranges;
import be.kwakeroni.parameters.client.api.model.Parameter;
import be.kwakeroni.parameters.client.api.model.ParameterGroup;
import be.kwakeroni.parameters.definition.api.ParameterGroupDefinition;
import be.kwakeroni.scratch.tv.Dag;
import be.kwakeroni.scratch.tv.DefaultParameter;
import be.kwakeroni.scratch.tv.Slot;

import java.util.function.Function;

import static be.kwakeroni.parameters.basic.definition.BasicGroup.*;
import static be.kwakeroni.parameters.types.support.ParameterTypes.STRING;

/**
 * Created by kwakeroni on 25/07/17.
 */
public interface AbstractMappedRangedTV extends ParameterGroup<Mapped<Dag, Ranged<Slot, Simple>>> {

    public static Parameter<Dag> DAY = new DefaultParameter<>("day", Dag.type);
    public static Parameter<Range<Slot>> SLOT = new DefaultParameter<>("slot", Ranges.rangeTypeOf(Slot.type));
    public static Parameter<String> PROGRAM = new DefaultParameter<>("program", STRING);

    public static ParameterGroupDefinition<Mapped<Dag, Ranged<Slot, Simple>>> definition(String name, Function<RangedDefinitionBuilder<?, ?>, RangedDefinitionBuilder<Slot, ?>> withRangeParameter) {
        return mappedGroup()
                .withKeyParameter(DAY.getName(), Dag.type)
                .mappingTo(withRangeParameter.apply(rangedGroup())
                        .mappingTo(group()
                                .withParameter(PROGRAM.getName())))
                .build(name);
    }

}
