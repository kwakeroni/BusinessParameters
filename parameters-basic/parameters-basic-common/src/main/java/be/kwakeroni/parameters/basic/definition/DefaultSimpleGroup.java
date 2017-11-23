package be.kwakeroni.parameters.basic.definition;

import be.kwakeroni.parameters.basic.client.model.Simple;
import be.kwakeroni.parameters.client.api.BusinessParameters;
import be.kwakeroni.parameters.client.api.model.EntryType;
import be.kwakeroni.parameters.client.api.model.ParameterGroup;
import be.kwakeroni.parameters.client.api.query.PartialQuery;
import be.kwakeroni.parameters.client.api.query.Query;

import java.util.Optional;

/**
 * Created by kwakeroni on 20/11/17.
 */
final class DefaultSimpleGroup<GroupType extends EntryType> implements Simple {

    private final ParameterGroup<GroupType> group;
    private final BusinessParameters businessParameters;
    private final PartialQuery<GroupType, Simple> partial;


    public DefaultSimpleGroup(ParameterGroup<GroupType> group, BusinessParameters businessParameters, PartialQuery<GroupType, Simple> partial) {
        this.group = group;
        this.businessParameters = businessParameters;
        this.partial = partial;
    }

    @Override
    public <T> Optional<T> get(Query<Simple, T> finalQuery) {
        Query<GroupType, T> query = partial.andThen(finalQuery);
        return businessParameters.get(group, query);
    }

}
