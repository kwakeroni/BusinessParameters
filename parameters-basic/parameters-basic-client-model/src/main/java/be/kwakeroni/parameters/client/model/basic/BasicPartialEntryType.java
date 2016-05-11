package be.kwakeroni.parameters.client.model.basic;

import be.kwakeroni.parameters.client.api.BusinessParameterGroup;
import be.kwakeroni.parameters.client.api.Entry;
import be.kwakeroni.parameters.client.api.Query;
import be.kwakeroni.parameters.client.model.EntryType;
import be.kwakeroni.parameters.client.model.Parameter;
import be.kwakeroni.parameters.client.query.basic.ValueQuery;

import java.util.function.Function;

/**
 * (C) 2016 Maarten Van Puymbroeck
 */
public class BasicPartialEntryType {

    public static void test(){
        Parameter<String> p = null;
        Query<Simple, String> value = new ValueQuery<>(p);

        Ranged<Character, Ranged<Integer, Simple>> group = null;

        String s = group.at('A').at(1).get(value);

        MyRanged<Ranged<Character, Ranged<Integer, Simple>>, Integer, Simple> groupAtAAt1 =
                new MyRanged<>(MySimple::new);

        EntryTypeConstructor<Ranged<Character, Ranged<Integer, Simple>>, Ranged<Integer, Simple>>
                intermediate =


        (BasicPartialQuery.PartialQuery<Ranged<Character, Ranged<Integer, Simple>>, Ranged<Integer, Simple>> pq) ->
                new MyOtherRanged<Integer, Simple>(MySimple::new)
                ;

        EntryTypeConstructor<Ranged<Character, Mapped<Simple>>, Mapped<Simple>>
                intermediate2 = EntryTypeConstructor.of(MyMapped::new, MySimple::new);


        MyOtherRanged<Character, Mapped<Simple>> other =
                new MyOtherRanged<>(intermediate2);




    }

    public static interface PartialEntryType<FullEntryType extends EntryType, MissingEntryType extends EntryType> {
        // MissingEntryType of(PartialQuery<FullEntryType, MissingEntryType> partialQuery);
    }

    public static class RangedPartialEntryType<FullEntryType extends EntryType, MissingEntryType extends EntryType> {

    }

    public static class Q<Missing extends EntryType, StillMissing extends EntryType> implements EntryTypeConstructor<Missing> {
        private Function<EntryTypeConstructor<StillMissing>, Missing> missingCreator;
        private EntryTypeConstructor< StillMissing> downConstructor;

        public Q(Function<EntryTypeConstructor<StillMissing>, Missing> missingCreator, EntryTypeConstructor<StillMissing> downConstructor) {
            this.missingCreator = missingCreator;
            this.downConstructor = downConstructor;
        }

        public <PartialEntryType extends EntryType> Missing create(BasicPartialQuery.PartialQuery<PartialEntryType, Missing> partialQuery){
            return missingCreator.apply(downConstructor);
        }
    }

    public static interface EntryTypeConstructor<MissingEntryType extends EntryType> {
        <PartialEntryType extends EntryType> MissingEntryType create(BasicPartialQuery.PartialQuery<PartialEntryType, MissingEntryType> partialQuery);

        static <Missing extends EntryType, StillMissing extends EntryType> EntryTypeConstructor< Missing>
            of(Function<EntryTypeConstructor<StillMissing>, Missing> missingCreator, EntryTypeConstructor< StillMissing> downConstructor){
             return new Q<>(missingCreator, downConstructor);
        }
    }
//
//    public static <Partial extends EntryType, V, Missing extends EntryType>
//        EntryTypeConstructor<Partial, Ranged<V, Missing>> createRanged(EntryTypeConstructor<Missing, StillMissing> downConstructor){
//        return EntryTypeConstructor.of(MyOtherRanged::new, MySimple::new);
//    }

    public static EntryTypeConstructor<Simple> createSimple(){
        return MySimple::new;
    }


    public static class MyRanged<FullEntryType extends EntryType, V, MissingEntryType extends EntryType>
            implements Ranged<V, MissingEntryType> {

        final EntryTypeConstructor<MissingEntryType>
                missingEntryConstructor;

        public  MyRanged(EntryTypeConstructor<MissingEntryType> missingEntryTypeConstructor){
            this.missingEntryConstructor = missingEntryTypeConstructor;
        }


        @Override
        public MissingEntryType at(V value) {
            //BasicPartialQuery.PartialQuery<Ranged<V, Simple>, Simple> partial = ;
            return missingEntryConstructor.create( BasicPartialQuery.at(value) );
        }
    }



    public static class MyOtherRanged<V, MissingEntryType extends EntryType>
            implements Ranged<V, MissingEntryType> {

        final EntryTypeConstructor<MissingEntryType>
                missingEntryConstructor;

        public MyOtherRanged(EntryTypeConstructor<MissingEntryType> missingEntryConstructor){
            this.missingEntryConstructor = missingEntryConstructor;
        }



        @Override
        public MissingEntryType at(V value) {
            //BasicPartialQuery.PartialQuery<Ranged<V, Ranged<Integer, Simple>>, Ranged<Integer, Simple>> partial ;

            return missingEntryConstructor.create(BasicPartialQuery.at(value));
        }
    }

    public static class MyMapped<MissingEntryType extends EntryType> implements Mapped<MissingEntryType> {
        final EntryTypeConstructor<MissingEntryType>
                missingEntryConstructor;

        public MyMapped(EntryTypeConstructor<MissingEntryType> missingEntryConstructor){
            this.missingEntryConstructor = missingEntryConstructor;
        }

        @Override
        public MissingEntryType forKey(String key) {
            return missingEntryConstructor.create(BasicPartialQuery.forKey(key));
        }
    }


    public static class MySimple<FullEntryType extends EntryType> implements Simple {

        final BasicPartialQuery.PartialQuery<FullEntryType, Simple> partial;

        public MySimple(BasicPartialQuery.PartialQuery<FullEntryType, Simple> partial){
            this.partial = partial;
        }

        @Override
        public <T> T value(Parameter<T> parameter) {
            return null;
        }

        @Override
        public Entry entry() {
            return null;
        }

        @Override
        public <T> T get(Query<Simple, T> simpleQuery) {
            return bp().get( partial.andThen(simpleQuery) );
        }

        private BusinessParameterGroup<FullEntryType> bp(){
            return null;
        }
    }

    public static interface EntryTypeBuilder<CurrentType extends EntryType, StillMissingType extends EntryType>

        extends EntryTypeConstructor<CurrentType>
    {


        CurrentType getEntryType();

        @Override
        <PartialEntryType extends EntryType> CurrentType create(BasicPartialQuery.PartialQuery<BasicPartialEntryType.PartialEntryType, CurrentType> partialQuery);
    }

    public static EntryTypeBuilder<Simple, ?> simple(){
        return null;
    }

    public static <ParentType extends EntryType> EntryTypeBuilder<Mapped<Simple>, Simple>
        mapped(EntryTypeBuilder<Simple, ?> builder){
        return new EntryTypeBuilder<Mapped<Simple>, Simple>() {
            @Override
            public Mapped<Simple> getEntryType() {
                return new MyMapped<>(builder);
            }

            @Override
            public <PartialEntryType extends EntryType> Mapped<Simple> create(BasicPartialQuery.PartialQuery<BasicPartialEntryType.PartialEntryType, Mapped<Simple>> partialQuery) {
                return new Mapped<Simple>() {
                    @Override
                    public Simple forKey(String key) {

                        BasicPartialQuery.PartialQuery<ParentType, Simple> subQuery =
                                partialQuery.andThen(BasicPartialQuery.forKey(key));
                        return builder.create(subQuery);
                    }
                };
            }


        };
    }

//    public static <V, MissingType extends EntryType, StillMissingType extends EntryType> EntryTypeBuilder<Ranged<V, MissingType>> ranged(EntryTypeBuilder<MissingType, StillMissingType> missing) {
//        return new EntryTypeBuilder<Ranged<V, MissingType>>(){
//            @Override
//            public Ranged<V, MissingType> getEntryType() {
//                EntryTypeConstructor<Ranged<V, MissingType>, MissingType> constructor = EntryTypeConstructor.of()
//                return new MyOtherRanged<V, MissingType>();
//            }
//        };
//
//    }

    public static <ParentType extends EntryType> EntryTypeBuilder<ParentType, Ranged<Character, Mapped<Simple>>,  Mapped<Simple>>
                        ranged(EntryTypeBuilder<Ranged<Character, Mapped<Simple>>, Mapped<Simple>, Simple> builder){
        return new EntryTypeBuilder<ParentType, Ranged<Character, Mapped<Simple>>, Mapped<Simple>>(){
//            @Override
//            public Ranged<V, MissingType> getEntryType() {
//                EntryTypeConstructor<Ranged<V, MissingType>, MissingType> constructor = EntryTypeConstructor.of()
//                return new MyOtherRanged<V, MissingType>();
//            }

            @Override
            public Ranged<Character, Mapped<Simple>> getEntryType() {
                return new MyOtherRanged<>(builder);
            }

            @Override
            public Ranged<Character, Mapped<Simple>> create(BasicPartialQuery.PartialQuery<ParentType, Ranged<Character, Mapped<Simple>>> partialQuery) {
                BasicPartialQuery.PartialQuery<Ranged<Character, Mapped<Simple>>, Mapped<Simple>> subQuery =
                        null;

                Mapped<Simple> mapped = builder.create(subQuery);

            }
        };
    }

    public static void test2(){

        Parameter<String> p = null;
        Query<Simple, String> value = new ValueQuery<>(p);

        EntryTypeConstructor<Ranged<Character, Mapped<Simple>>, Mapped<Simple>> rangedIntegerGroupConstructor =
                EntryTypeConstructor.of(MyMapped::new, MySimple::new);



        EntryTypeBuilder<Ranged<Character,  Mapped<Simple>>, ?> builder = ranged(mapped(simple()));

        Ranged<Character, Mapped<Simple>> group = builder.getEntryType();

        Mapped<Simple> atA = group.at('A');

        Simple atA1 = atA.forKey("1");

        String s = atA1.get(value);
    }
}
