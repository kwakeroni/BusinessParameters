package be.kwakeroni.parameters.definition.api.catalog;

import be.kwakeroni.parameters.definition.api.ParameterGroupDefinition;

import java.util.Iterator;
import java.util.stream.Stream;

/**
 * Created by kwakeroni on 26/04/17.
 */
public interface ParameterGroupDefinitionCatalog extends Iterable<ParameterGroupDefinition> {

    public Stream<ParameterGroupDefinition> stream();

    @Override
    public default Iterator<ParameterGroupDefinition> iterator() {
        return stream().iterator();
    }
}
