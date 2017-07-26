package be.kwakeroni.scratch.tv;

import be.kwakeroni.parameters.definition.api.ParameterGroupDefinition;
import be.kwakeroni.parameters.definition.api.catalog.ParameterGroupDefinitionCatalog;
import be.kwakeroni.scratch.tv.definition.*;

import java.util.ArrayList;
import java.util.stream.Stream;

/**
 * Created by kwakeroni on 26/04/17.
 */
public class Catalog extends ArrayList<ParameterGroupDefinition> implements ParameterGroupDefinitionCatalog {

    private static final ParameterGroupDefinition[] DEFINITIONS = {
            SimpleTV.DEFINITION,
            MappedTV.DEFINITION,
            RangedFilterTV.DEFINITION,
            RangedQueryTV.DEFINITION,
            MappedRangedFilterTV.DEFINITION,
            MappedRangedQueryTV.DEFINITION
    };

    public Catalog() {
        super(DEFINITIONS.length);
        for (ParameterGroupDefinition def : DEFINITIONS) {
            this.add(def);
        }
    }

    @Override
    public Stream<ParameterGroupDefinition> stream() {
        return super.stream();
    }
}
