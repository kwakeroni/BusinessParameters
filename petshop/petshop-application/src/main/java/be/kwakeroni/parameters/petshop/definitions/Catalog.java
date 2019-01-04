package be.kwakeroni.parameters.petshop.definitions;

import be.kwakeroni.parameters.definition.api.ParameterGroupDefinition;
import be.kwakeroni.parameters.definition.api.catalog.ParameterGroupDefinitionCatalog;

import java.util.stream.Stream;

/**
 * Created by kwakeroni on 06/11/17.
 */
public class Catalog implements ParameterGroupDefinitionCatalog {

    @Override
    public Stream<ParameterGroupDefinition<?>> stream() {
        return Stream.of(
                ContactDetails.DEFINITION,
                AnimalPrice.DEFINITION,
                BulkDiscount.DEFINITION,
                SalesDiscount.DEFINITION
        );
    }

}

