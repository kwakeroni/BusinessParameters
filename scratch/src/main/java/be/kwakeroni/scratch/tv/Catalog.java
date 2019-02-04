package be.kwakeroni.scratch.tv;

import be.kwakeroni.parameters.definition.api.ParameterGroupDefinition;
import be.kwakeroni.parameters.definition.api.catalog.ParameterGroupDefinitionCatalog;
import be.kwakeroni.scratch.tv.definition.HistoricizedTV;
import be.kwakeroni.scratch.tv.definition.MappedRangedFilterTV;
import be.kwakeroni.scratch.tv.definition.MappedRangedQueryTV;
import be.kwakeroni.scratch.tv.definition.MappedTV;
import be.kwakeroni.scratch.tv.definition.RangedFilterTV;
import be.kwakeroni.scratch.tv.definition.RangedQueryTV;
import be.kwakeroni.scratch.tv.definition.SimpleTV;

import java.util.Arrays;
import java.util.stream.Stream;

/**
 * Created by kwakeroni on 26/04/17.
 */
public class Catalog implements ParameterGroupDefinitionCatalog {

    private static final ParameterGroupDefinition<?>[] DEFINITIONS = {
            SimpleTV.DEFINITION,
            MappedTV.DEFINITION,
            HistoricizedTV.DEFINITION,
            RangedFilterTV.DEFINITION,
            RangedQueryTV.DEFINITION,
            MappedRangedFilterTV.DEFINITION,
            MappedRangedQueryTV.DEFINITION
    };

    @Override
    public Stream<ParameterGroupDefinition<?>> stream() {
        return Arrays.stream(DEFINITIONS);
    }

}
