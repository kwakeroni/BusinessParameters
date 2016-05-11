package be.kwakeroni.parameters.client.model.basic;

import be.kwakeroni.parameters.client.api.Query;
import be.kwakeroni.parameters.client.model.EntryType;
import be.kwakeroni.parameters.client.model.Parameter;
import be.kwakeroni.parameters.client.query.basic.MappedQuery;
import be.kwakeroni.parameters.client.query.basic.RangedQuery;
import be.kwakeroni.parameters.client.query.basic.ValueQuery;

/**
 * (C) 2016 Maarten Van Puymbroeck
 */
public class BasicPartialQuery {

    public static void test(){
        Parameter<String> p = null;
        Query<Simple, String> getValue = new ValueQuery<>(p);

        PartialQuery<Ranged<Integer, Simple>, Simple> at1 =
                at(1);

        Query<Ranged<Integer, Simple>, String> query = at1.andThen(getValue);



        PartialQuery<Ranged<Character, Ranged<Integer, Simple>>, Ranged<Integer, Simple>> atA =
                at('A');

        PartialQuery<Ranged<Character, Ranged<Integer, Simple>>, Simple> atAat1 = atA.andThen(at1);

        Query<Ranged<Character, Ranged<Integer, Simple>>, String> valueAtA1 = atA.andThen(at(1)).andThen(getValue);
    }

    public static interface PartialQuery<FullEntryType extends EntryType, MissingEntryType extends EntryType> {
        <T> Query<FullEntryType, T> andThen(Query<MissingEntryType, T> downQuery);

        default <StillMissingType extends EntryType> PartialQuery<FullEntryType, StillMissingType>
            andThen(PartialQuery<MissingEntryType, StillMissingType> anotherPartial){
            return new PartialQuery<FullEntryType, StillMissingType>() {
                @Override
                public <T> Query<FullEntryType, T> andThen(Query<StillMissingType, T> downQuery) {
                    Query<MissingEntryType, T> query = anotherPartial.andThen(downQuery);
                    return PartialQuery.this.andThen(query);
                }
            };
        }

        static <ET extends EntryType>
                PartialQuery<ET, ET>
            identity(){
            return new PartialQuery<ET, ET>() {
                @Override
                public <T> Query<ET, T> andThen(Query<ET, T> downQuery) {
                    return downQuery;
                }
            };
        }
    }

    public static <V, MissingEntryType extends EntryType>
      PartialQuery<Ranged<V, MissingEntryType>, MissingEntryType>
      at(V value){
        return new PartialQuery<Ranged<V, MissingEntryType>, MissingEntryType>() {
            @Override
            public <T> Query<Ranged<V, MissingEntryType>, T> andThen(Query<MissingEntryType, T> downQuery) {
                return new RangedQuery<>(value, downQuery);
            }
        };
    }

    public static <MissingEntryType extends EntryType>
        PartialQuery<Mapped<MissingEntryType>, MissingEntryType>
        forKey(String key){
            return new PartialQuery<Mapped<MissingEntryType>, MissingEntryType>() {
                @Override
                public <T> Query<Mapped<MissingEntryType>, T> andThen(Query<MissingEntryType, T> downQuery) {
                    return new MappedQuery<>(key, downQuery);
                }
            };
    }


}
