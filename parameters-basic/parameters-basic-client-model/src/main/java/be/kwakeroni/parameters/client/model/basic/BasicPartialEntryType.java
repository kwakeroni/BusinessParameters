package be.kwakeroni.parameters.client.model.basic;

import be.kwakeroni.parameters.client.api.BusinessParameterGroup;
import be.kwakeroni.parameters.client.api.Entry;
import be.kwakeroni.parameters.client.api.Query;
import be.kwakeroni.parameters.client.model.EntryType;
import be.kwakeroni.parameters.client.model.Parameter;
import be.kwakeroni.parameters.client.query.basic.ValueQuery;

/**
 * (C) 2016 Maarten Van Puymbroeck
 */
public class BasicPartialEntryType {

    public static interface EntryTypeBuilder<FullEntryType extends EntryType, BuiltType extends EntryType>

    {

        BuiltType create(BasicPartialQuery.PartialQuery<FullEntryType, BuiltType> partialQuery);

        default FullEntryType getEntryType() {
            // FullEntryType == BuiltType
            return (FullEntryType) create(unrecognizedIdentity());
        }

        default <ParentType extends EntryType> PartialEntryType<ParentType, BuiltType>
                with(BasicPartialQuery.PartialQuery<FullEntryType, ParentType> partialQuery){

            return myQuery -> create( partialQuery.andThen(myQuery) );

        }
    }

    public static interface PartialEntryType<CurrentType extends EntryType, MissingType extends EntryType> {
        MissingType appendQuery(BasicPartialQuery.PartialQuery<CurrentType, MissingType> myQuery);
    }


    public static <FullEntryType extends EntryType, V, MissingType extends EntryType> EntryTypeBuilder<FullEntryType, Ranged<V, MissingType>>
    ranged(EntryTypeBuilder<FullEntryType, MissingType> builder){
        return (partialQuery) -> new Ranged<V, MissingType>(){
            @Override
            public MissingType at(V value) {
                return builder.with(partialQuery).appendQuery(BasicPartialQuery.at(value));
            }
        };
    }

    public static <FullEntryType extends EntryType, MissingType extends EntryType> EntryTypeBuilder<FullEntryType, Mapped<MissingType>>
    mapped(EntryTypeBuilder<FullEntryType, MissingType> builder){
        return (partialQuery) -> new Mapped<MissingType>(){
            @Override
            public MissingType forKey(String key) {
                return builder.with(partialQuery).appendQuery(BasicPartialQuery.forKey(key));
            }
        };
    }

    public static <FullEntryType extends EntryType> EntryTypeBuilder<FullEntryType, Simple>
    simple(BusinessParameterGroup<FullEntryType> group){
        return (partialQuery) -> new MySimple<>(group, partialQuery);
    }




    public static class MySimple<FullEntryType extends EntryType>
            implements Simple {

        final BusinessParameterGroup<FullEntryType> group;
        final BasicPartialQuery.PartialQuery<FullEntryType, Simple> partial;



        public MySimple(BusinessParameterGroup<FullEntryType> group, BasicPartialQuery.PartialQuery<FullEntryType, Simple> partial){
            this.group = group;
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
            return group.get( partial.andThen(simpleQuery) );
        }

    }



    private static final <ET extends EntryType> BasicPartialQuery.PartialQuery<ET, ET> identity(){
        return BasicPartialQuery.PartialQuery.<ET> identity();
    }

    private static final <FullEntryType extends EntryType, ET extends EntryType> BasicPartialQuery.PartialQuery<FullEntryType, ET> unrecognizedIdentity(){
        return (BasicPartialQuery.PartialQuery<FullEntryType, ET>) BasicPartialQuery.PartialQuery.<ET> identity();
    }
    public static void test2(){

        Parameter<String> p = null;
        Query<Simple, String> value = new ValueQuery<>(p);

        BusinessParameterGroup<Ranged<Character, Mapped<Simple>>> bp = null;

        simple(bp).getEntryType();

        EntryTypeBuilder<Ranged<Character,  Mapped<Simple>>, Ranged<Character,  Mapped<Simple>>> builder = ranged(mapped(simple(bp)));

        Ranged<Character, Mapped<Simple>> group = builder.getEntryType();

        Mapped<Simple> atA = group.at('A');

        Simple atA1 = atA.forKey("1");

        String s = atA1.get(value);

        group.at('A').forKey("1").get(value);
    }
}

