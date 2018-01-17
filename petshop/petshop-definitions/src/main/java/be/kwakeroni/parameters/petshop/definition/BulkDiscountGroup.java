package be.kwakeroni.parameters.petshop.definition;

import be.kwakeroni.parameters.definition.api.ParameterGroupDefinition;
import be.kwakeroni.parameters.types.support.ParameterTypes;

import static be.kwakeroni.parameters.basic.definition.BasicGroup.*;

/**
 * Created by kwakeroni on 18/08/17.
 */
public class BulkDiscountGroup {

    public static String NAME = "be.kwakeroni.parameters.petshop.bulk-discount";
//    public static Parameter<Integer> DISCOUNT_PERCENTAGE = P

    public static ParameterGroupDefinition DEFINITION =
            mappedGroup()
                    .withKeyParameter("type")
                    .mappingTo(rangedGroup()
                            .withRangeParameter("number", ParameterTypes.INT))
                    .mappingTo(group()
                            .withParameter("discountPercentage"))
                    .build(NAME);

}
