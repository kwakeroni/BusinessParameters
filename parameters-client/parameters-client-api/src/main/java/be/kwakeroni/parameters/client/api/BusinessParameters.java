package be.kwakeroni.parameters.client.api;

import be.kwakeroni.parameters.client.api.query.Query;
import be.kwakeroni.parameters.client.api.model.EntryType;
import be.kwakeroni.parameters.client.api.model.ParameterGroup;

/**
 * Retrieves values of business parameters.
 */
public interface BusinessParameters {

    public <ET extends EntryType, T> T get(ParameterGroup<ET> group, Query<ET, T> query);

    public default <ET extends EntryType> BusinessParameterGroup<ET> forGroup(final ParameterGroup<ET> group){
        class GroupWrapper implements BusinessParameterGroup<ET> {
            @Override
            public String getName() {
                return group.getName();
            }

            @Override
            public <T> T get(Query<ET, T> query) {
                return BusinessParameters.this.get(group, query);
            }
        };

        return new GroupWrapper();
    }
}
