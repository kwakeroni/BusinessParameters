package be.kwakeroni.parameters.client.model.basic;

import be.kwakeroni.parameters.client.api.*;
import be.kwakeroni.parameters.client.model.EntryType;
import be.kwakeroni.parameters.client.model.Parameter;
import be.kwakeroni.parameters.client.query.basic.EntryQuery;
import be.kwakeroni.parameters.client.query.basic.RangedQuery;
import be.kwakeroni.parameters.client.query.basic.ValueQuery;

import java.util.function.Function;

/**
 * (C) 2016 Maarten Van Puymbroeck
 */
public class BasicEntryType {

    public static <V, ET extends EntryType> EntryTypeBuilder<Ranged<V, ET>, ET> ranged(Class<V> valueType, EntryTypeBuilder<ET, ?> andThen){
        return () ->
                new Ranged<V, ET>() {
                    @Override
                    public ET at(V value) {
                        return null;
                    }
                }
                ;
    }





    public static <FullEntryType extends EntryType, V, MissingEntryType extends EntryType>
        PartialEntryType<FullEntryType, Ranged<V, MissingEntryType>> _ranged(){

        return new PartialEntryType<FullEntryType, Ranged<V, MissingEntryType>>() {
            @Override
            public  Ranged<V, MissingEntryType> of(PartialQuery<FullEntryType, Ranged<V, MissingEntryType>> partialQuery) {
                return ranged(partialQuery);
            }
        };
    }

    private static <V, MissingEntryType extends EntryType>
        Ranged<V, MissingEntryType> ranged(PartialEntryType<Ranged<V, MissingEntryType>, MissingEntryType> partialEntryType) {
        return new Ranged<V, MissingEntryType>() {
            @Override
            public MissingEntryType at(V value) {



                return partialEntryType.of(partialQuery);
            }
        };
    }

    public static <FullEntryType extends EntryType> PartialEntryType<FullEntryType, Simple> _simple(final BusinessParameterGroup<FullEntryType> group){
        return new PartialEntryType<FullEntryType, Simple>() {
            @Override
            public  Simple of(PartialQuery<FullEntryType, Simple> partialQuery) {
                return simple(partialQuery, group);
            }
        };
    }



    private static <FullEntryType extends EntryType> Simple simple(PartialQuery<FullEntryType, Simple> partialQuery, BusinessParameterGroup<FullEntryType> group){
        return new Simple() {
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
                Query<FullEntryType, T> q = partialQuery.andThen(simpleQuery);
                return group.get(q);
            }
        };
    }

    public static void test(){
        Simple simple = null;
        Parameter<String> p = null;
        String v = simple.get(new ValueQuery<>(p));

        BusinessParameterGroup<Ranged<Integer, Simple>> bp = null;

        Ranged<Integer, Simple> ranged = _ranged(bp);
        String r = ranged.at(1).get(new ValueQuery<>(p));
    }

    public static EntryTypeBuilder<Simple, Simple> in(BusinessParameterGroup<?> group){
        return () -> new Simple() {
            @Override
            public <T> T value(Parameter<T> parameter) {
                return get(new ValueQuery<>(parameter));
            }

            @Override
            public Entry entry() {
                return get(new EntryQuery());
            }

            @Override
            public <T> T get(Query<Simple, T> simpleQuery) {
                return group.get(firstQuery.andThen(simpleQuery));
            }
        };
    }
//
//
//    public static Simple simple(BusinessParameterGroup<Simple> group){
//        return simple(group, QuerySupplier.root());
//    }
//
//    public static <ET extends EntryType> Simple simple(BusinessParameterGroup<ET> group, QuerySupplier<Simple, ET> firstQuery){
//
//    }
//
//    public static <V, UP extends EntryType> Ranged<V, Simple> ranged(BusinessParameterGroup<UP> group, QuerySupplier<Ranged<V, Simple>, UP> firstQuery){
//
//        return ranged(group, firstQuery, BasicEntryType::simple);
//    }
//
//    public static <V, UP extends EntryType, DOWN extends EntryType> Ranged<V, DOWN> ranged(BusinessParameterGroup<UP> group, QuerySupplier<Ranged<V, DOWN>, UP> firstQuery, Criterion<UP, DOWN> downQuery){
//        return new Ranged<V, DOWN>() {
//            @Override
//            public DOWN at(V value) {
//
//
//                QuerySupplier<DOWN, Ranged<V, DOWN>> myQuery = new QuerySupplier<DOWN, Ranged<V, DOWN>>() {
//                    @Override
//                    public <T> Query<Ranged<V, DOWN>, T> andThen(Query<DOWN, T> subQuery) {
//                        return new RangedQuery<>(value, subQuery);
//                    }
//                };
//
//
//                return downQuery.of(group, firstQuery.andThen(myQuery));
//            }
//
//
//        };
//    }

//    public static Mapped<Simple> mapped(){
//        return mapped(simple());
//    }
//
//    public static <ET extends EntryType> Mapped<ET> mapped(ET entryType){
//        return new Mapped<ET>() {
//        };
//    }

}
