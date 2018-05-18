package be.kwakeroni.parameters.petshop.definitions;

import be.kwakeroni.parameters.basic.client.model.Mapped;
import be.kwakeroni.parameters.basic.client.model.Ranged;
import be.kwakeroni.parameters.basic.client.model.Simple;
import be.kwakeroni.parameters.basic.definition.BasicGroup;
import be.kwakeroni.parameters.client.api.model.Parameter;
import be.kwakeroni.parameters.core.support.client.ParameterSupport;
import be.kwakeroni.parameters.definition.api.ParameterGroupDefinition;
import be.kwakeroni.parameters.types.support.ParameterTypes;

public class BulkDiscount {

    public static final Parameter<String> ANIMAL = ParameterSupport.ofString("animal");
    public static final Parameter<Integer> QUANTITY = ParameterSupport.ofInt("quantity");
    public static final Parameter<Integer> DISCOUNT = ParameterSupport.ofInt("discount");

    public static final ParameterGroupDefinition<Mapped<String, Ranged<Integer, Simple>>> DEFINITION =
            BasicGroup.mappedGroup()
                    .withKeyParameter("animal", ParameterTypes.STRING)
                    .mappingTo(
                            BasicGroup.rangedGroup()
                                    .withRangeParameter("quantity", ParameterTypes.INT)
                                    .mappingTo(BasicGroup.group()
                                            .withParameter("discount"))
                    ).build("petshop.bulk-discount");
}
