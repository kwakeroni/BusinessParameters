package be.kwakeroni.parameters.client.api;

import be.kwakeroni.parameters.client.model.EntryType;
import be.kwakeroni.parameters.client.model.Parameter;
import be.kwakeroni.parameters.client.model.ParameterGroup;

/**
 * Retrieves values of business parameters of a specific group.
 */
public interface BusinessParameterGroup<ET extends EntryType> {

    <T> T get(Query<ET, T> query);

    default <T> T get(QueryBuilder<ET, T> query){
        return get(query.build());
    }

//    ET retrieve();

    /**
     * Retrieves the value of a specific business parameter, identified by the given identifier.
     * @param parameter The parameter for which to retrieve the value
     * @param identifier The identifier of the entry of the desired value
     * @param <T> Type of the parameter
     * @return Value of the parameter
     */
//    <T> T getValue(Parameter<T> parameter, EntryIdentifier<ET> identifier);

    /**
     * Retrieves an entry of the parameter group.
     * @param identifier The identifier of the entry
     * @return An entry with its parameter values
     */
//    Entry getEntry(EntryIdentifier<ET> identifier);

}
