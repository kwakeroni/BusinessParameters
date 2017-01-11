package be.kwakeroni.parameters_exp.client.api.query;


import be.kwakeroni.parameters.client.api.model.EntryType;
import be.kwakeroni.parameters.client.api.query.Query;

/**
 * Represents part of a support to retrieve business parameters.
 * A support always targets a type of entry.
 * A partial support targets a certain entry type, but is &quot;missing&quot; a part of the complete support.
 * A partial support can be supplied with a complementary support (targeting the missing type) to produce a complete support (targeting the final type).
 * A partial support can also be supplied with another partial support, to produce a more complete, but still partial support.
 * (C) 2016 Maarten Van Puymbroeck
 *
 * @param <Target> The final type of the entry targeted by this support
 * @param <Missing> The type of the entry targeted by the missing part of this support
 */
public interface PartialQuery<Target extends EntryType, Missing extends EntryType> {


    /**
     * Produces a complete support consisting of this partial support and a missing support.
     * @param missingQuery The missing support, targetting the {@code Missing} entry type.
     * @param <T> The result type of the support
     * @return complete support targetting the entry type of this partial support.
     */
    <T> Query<Target, T> andThen(Query<Missing, T> missingQuery);

    /**
     * Produces a partial support consisting of this partial support and part of the missing support.
     * @param anotherPart Part of the missing support, targetting the {@code Missing} entry type, but still missing a support targetting the {@code StillMissing} entry type.
     * @param <StillMissing> The type of the entry targeted by the missing part of the new partial support.
     * @return partial support missing the {@code StillMissing} part of the support.
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
                return "starting support";
            }
        };
    }

}
