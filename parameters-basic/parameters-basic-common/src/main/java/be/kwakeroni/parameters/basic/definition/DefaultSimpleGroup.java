package be.kwakeroni.parameters.basic.definition;

import be.kwakeroni.parameters.basic.client.model.Simple;
import be.kwakeroni.parameters.client.api.BusinessParameters;
import be.kwakeroni.parameters.client.api.model.EntryType;
import be.kwakeroni.parameters.client.api.query.PartialQuery;
import be.kwakeroni.parameters.client.api.query.Query;
import be.kwakeroni.parameters.definition.ext.PartialGroup;

import java.util.Optional;

final class DefaultSimpleGroup<GroupType extends EntryType> implements PartialGroup<GroupType, Simple> {

    private final String name;
    private final BusinessParameters businessParameters;

    DefaultSimpleGroup(String name, BusinessParameters businessParameters) {
        this.name = name;
        this.businessParameters = businessParameters;
    }

    @Override
    public Simple resolve(PartialQuery<GroupType, Simple> parentQuery) {
        return new Resolved(parentQuery);
    }

    private final /* value */ class Resolved implements Simple {

        private final PartialQuery<GroupType, Simple> parentQuery;

        Resolved(PartialQuery<GroupType, Simple> parentQuery) {
            this.parentQuery = parentQuery;
        }

        @Override
        public <T> Optional<T> get(Query<Simple, T> finalQueryPart) {
            Query<GroupType, T> myQuery = parentQuery.andThen(finalQueryPart);
            return businessParameters.get(() -> name, myQuery);
        }

    }

}
