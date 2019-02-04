package be.kwakeroni.parameters.petshop.definitions;

import be.kwakeroni.parameters.basic.client.model.Historicized;
import be.kwakeroni.parameters.basic.client.model.Mapped;
import be.kwakeroni.parameters.basic.client.model.Simple;
import be.kwakeroni.parameters.basic.definition.BasicGroup;
import be.kwakeroni.parameters.client.api.model.Parameter;
import be.kwakeroni.parameters.core.support.client.ParameterSupport;
import be.kwakeroni.parameters.definition.api.ParameterGroupDefinition;
import be.kwakeroni.parameters.types.support.ParameterTypes;

import java.time.LocalDate;

public class SalesDiscount {

    public static final Parameter<String> ANIMAL = ParameterSupport.ofString("animal");
    public static final Parameter<LocalDate> PERIOD = ParameterSupport.ofLocalDate("period");
    public static final Parameter<Integer> DISCOUNT = ParameterSupport.ofInt("discount");

    public static final ParameterGroupDefinition<Mapped<String, Historicized<Simple>>> DEFINITION =
            BasicGroup.mappedGroup()
                    .withKeyParameter("animal", ParameterTypes.STRING)
                    .mappingTo(
                            BasicGroup.historicizedGroup()
                                    .withParameter("period")
                                    .mappingTo(BasicGroup.group()
                                            .withParameter("discount"))
                    ).build("petshop.sales-discount");
}
