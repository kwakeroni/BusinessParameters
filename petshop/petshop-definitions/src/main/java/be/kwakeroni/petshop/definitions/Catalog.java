package be.kwakeroni.petshop.definitions;

import be.kwakeroni.parameters.definition.api.ParameterGroupDefinition;
import be.kwakeroni.parameters.definition.api.catalog.ParameterGroupDefinitionCatalog;
import be.kwakeroni.parameters.types.support.ParameterTypes;

import java.util.stream.Stream;

import static be.kwakeroni.parameters.basic.definition.BasicGroup.*;

/**
 * Created by kwakeroni on 06/11/17.
 */
public class Catalog implements ParameterGroupDefinitionCatalog {

    ParameterGroupDefinition SALES = rangedGroup()
            .withRangeParameter("period", ParameterTypes.INT)
            .mappingTo(
                    mappedGroup()
                            .withKeyParameter("species")
                            .mappingTo(
                                    group()
                                            .withParameter("percentage")
                            )
            ).build("petshop.sales");
    ;

    @Override
    public Stream<ParameterGroupDefinition> stream() {
        return Stream.of(SALES);
    }

}

