package be.kwakeroni.parameters.petshop.definitions;

import be.kwakeroni.parameters.basic.client.model.Mapped;
import be.kwakeroni.parameters.basic.client.model.Simple;
import be.kwakeroni.parameters.basic.definition.BasicGroup;
import be.kwakeroni.parameters.client.api.model.Parameter;
import be.kwakeroni.parameters.core.support.client.ParameterSupport;
import be.kwakeroni.parameters.definition.api.ParameterGroupDefinition;
import be.kwakeroni.parameters.types.support.ParameterTypes;

public class AnimalPrice {

    public static final Parameter<String> ANIMAL = ParameterSupport.ofString("animal");
    public static final Parameter<Integer> PRICE = ParameterSupport.ofInt("price");

    public static final ParameterGroupDefinition<Mapped<String, Simple>> DEFINITION =
            BasicGroup.mappedGroup()
                    .withKeyParameter("animal", ParameterTypes.STRING)
                    .mappingTo(BasicGroup.group()
                            .withParameter("price")
                    ).build("petshop.price");


}
