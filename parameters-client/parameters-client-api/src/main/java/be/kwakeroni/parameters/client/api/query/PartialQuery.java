package be.kwakeroni.parameters.client.api.query;

import be.kwakeroni.parameters.client.api.model.EntryType;

/**
 * Created by kwakeroni on 10/11/17.
 */
public interface PartialQuery<GroupType extends EntryType, SubEntryType extends EntryType> {

    public <T> Query<GroupType, T> andThen(Query<SubEntryType, T> subQuery);

    public default <MissingType extends EntryType> PartialQuery<GroupType, MissingType> andThen(PartialQuery<SubEntryType, MissingType> partialQuery) {
        return new PartialQuery<GroupType, MissingType>() {
            @Override
            public <T> Query<GroupType, T> andThen(Query<MissingType, T> subQuery) {
                Query<SubEntryType, T> query = partialQuery.andThen(subQuery);
                return PartialQuery.this.andThen(query);
            }
        };
    }

    public static <GroupType extends EntryType> PartialQuery<GroupType, GroupType> identity() {
        return new PartialQuery<GroupType, GroupType>() {
            @Override
            public <T> Query<GroupType, T> andThen(Query<GroupType, T> subQuery) {
                return subQuery;
            }
        };
    }
}
