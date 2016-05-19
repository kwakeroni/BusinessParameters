package be.kwakeroni.parameters.client.api.query;

import be.kwakeroni.parameters.client.model.EntryType;

/**
 * Represents part of a query to retrieve business parameters.
 * A query always targets a type of entry.
 * A partial query targets a certain entry type, but is &quot;missing&quot; a part of the complete query.
 * A partial query can be supplied with a complementary query (targeting the missing type) to produce a complete query (targeting the final type).
 * A partial query can also be supplied with another partial query, to produce a more complete, but still partial query.
 * (C) 2016 Maarten Van Puymbroeck
 *
 * @param <Target> The final type of the entry targeted by this query
 * @param <Missing> The type of the entry targeted by the missing part of this query
 */
public interface PartialQuery<Target extends EntryType, Missing extends EntryType> {


    /**
     * Produces a complete query consisting of this partial query and a missing query.
     * @param missingQuery The missing query, targetting the {@code Missing} entry type.
     * @param <T> The result type of the query
     * @return complete query targetting the entry type of this partial query.
     */
    <T> Query<Target, T> andThen(Query<Missing, T> missingQuery);

    /**
     * Produces a partial query consisting of this partial query and part of the missing query.
     * @param anotherPart Part of the missing query, targetting the {@code Missing} entry type, but still missing a query targetting the {@code StillMissing} entry type.
     * @param <StillMissing> The type of the entry targeted by the missing part of the new partial query.
     * @return partial query missing the {@code StillMissing} part of the query.
     */
    default <StillMissing extends EntryType> PartialQuery<Target, StillMissing> andThen(PartialQuery<Missing, StillMissing> anotherPart){
        return new PartialQuery<Target, StillMissing>() {
            @Override
            public <T> Query<Target, T> andThen(Query<StillMissing, T> downQuery) {
                return PartialQuery.this.andThen( anotherPart.andThen(downQuery) );
            }

            public String toString(){
                return PartialQuery.this + " and then " + anotherPart + " and then ?";
            }
        };
    }


    static <Target extends EntryType> PartialQuery<Target, Target> startQuery(){
        return new PartialQuery<Target, Target>() {
            @Override
            public <T> Query<Target, T> andThen(Query<Target, T> anotherPart) {
                return anotherPart;
            }

            public String toString(){
                return "starting query";
            }
        };
    }

}
