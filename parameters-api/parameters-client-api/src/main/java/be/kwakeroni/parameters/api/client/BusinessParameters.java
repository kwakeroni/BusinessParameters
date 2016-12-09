package be.kwakeroni.parameters.api.client;

import be.kwakeroni.parameters.api.client.query.Query;
import be.kwakeroni.parameters.api.client.model.EntryType;
import be.kwakeroni.parameters.api.client.model.ParameterGroup;

/**
 * Retrieves values of business parameters.
 */
public interface BusinessParameters {

    <ET extends EntryType, T> T get(ParameterGroup<ET> group, Query<ET, T> query);

//    /**
//     * Retrieves the value of a specific business parameter, identified by the given identifier.
//     * @param group The group of the parameter
//     * @param parameter The parameter for which to retrieve the value
//     * @param identifier The identifier of the entry of the desired value
//     * @param <ET> Type of the group and its entries
//     * @param <T> Type of the parameter
//     * @return Value of the parameter
//     */
//    <ET extends EntryType, T> T getValue(ParameterGroup<ET> group, Parameter<T> parameter, EntryIdentifier<ET> identifier);

//    /**
//     * Retrieves an entry of a business parameter group.
//     * @param group The group for which to retrieve the entry
//     * @param identifier The identifier of the entry
//     * @param <ET> Type of the group and its entries
//     * @return An entry with its parameter values
//     */
//    <ET extends EntryType> Entry getEntry(ParameterGroup<ET> group, EntryIdentifier<ET> identifier);

    default <ET extends EntryType> BusinessParameterGroup<ET> forGroup(final ParameterGroup<ET> group){
        return new BusinessParameterGroup<ET>() {
//            @Override
//            public <T> T getValue(Parameter<T> parameter, EntryIdentifier<ET> identifier) {
//                return BusinessParameters.this.getValue(group, parameter, identifier);
//            }
//
//            @Override
//            public Entry getEntry(EntryIdentifier<ET> identifier) {
//                return BusinessParameters.this.getEntry(group, identifier);
//            }


            @Override
            public String getName() {
                return group.getName();
            }

            @Override
            public <T> T get(Query<ET, T> query) {
                return BusinessParameters.this.get(group, query);
            }

//            @Override
//            public ET retrieve() {
//                return group.retrieve();
//            }
        };
    }


}
