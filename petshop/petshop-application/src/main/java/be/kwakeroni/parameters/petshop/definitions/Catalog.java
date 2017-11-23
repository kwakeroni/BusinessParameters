package be.kwakeroni.parameters.petshop.definitions;

import be.kwakeroni.parameters.basic.client.model.Mapped;
import be.kwakeroni.parameters.basic.client.model.Ranged;
import be.kwakeroni.parameters.basic.client.model.Simple;
import be.kwakeroni.parameters.definition.api.ParameterGroupDefinition;
import be.kwakeroni.parameters.definition.api.catalog.ParameterGroupDefinitionCatalog;
import be.kwakeroni.parameters.types.support.ParameterTypes;

import java.util.stream.Stream;

import static be.kwakeroni.parameters.basic.definition.BasicGroup.*;

/**
 * Created by kwakeroni on 06/11/17.
 */
public class Catalog implements ParameterGroupDefinitionCatalog {


    ParameterGroupDefinition<Mapped<String, Ranged<Integer, Simple>>> SALES =
            mappedGroup()
                    .withKeyParameter("species")
                    .mappingTo(
                            rangedGroup()
                                    .withRangeParameter("quantity", ParameterTypes.INT)
                                    .mappingTo(
                                            group()
                                                    .withParameter("percentage")
                                    )
                    ).build("petshop.sales");

    @Override
    public Stream<ParameterGroupDefinition<?>> stream() {
        return Stream.of(SALES);
    }

}

