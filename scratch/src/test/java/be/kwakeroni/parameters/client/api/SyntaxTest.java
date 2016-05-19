package be.kwakeroni.parameters.client.api;

import be.kwakeroni.parameters.client.api.query.Query;
import be.kwakeroni.parameters.client.model.EntryType;
import be.kwakeroni.parameters.client.model.Parameter;
import be.kwakeroni.parameters.client.model.ParameterGroup;
import be.kwakeroni.parameters.client.model.basic.Ranged;
import be.kwakeroni.parameters.client.model.basic.Simple;
import be.kwakeroni.parameters.client.query.basic.BasicQueries;
import be.kwakeroni.parameters.client.query.basic.RangedQuery;
import be.kwakeroni.parameters.client.query.basic.ValueQuery;
import org.junit.Test;

import java.time.DayOfWeek;

import static be.kwakeroni.parameters.client.model.basic.BasicEntryTypes.*;
import static be.kwakeroni.parameters.client.query.basic.BasicQueries.*;

public class SyntaxTest {

    private BusinessParameters businessParameters = new BusinessParameters() {
        @Override
        public <ET extends EntryType, T> T get(ParameterGroup<ET> group, Query<ET, T> query) {
            System.out.println(group.getName()+"."+query);
            return null;
        }

    };
//    private BusinessParameterGroup<Simple> simple = businessParameters.forGroup(SIMPLE);
//    private BusinessParameterGroup<Ranged<Integer, Simple>> ranged = businessParameters.forGroup(RANGED);
//    private BusinessParameterGroup<Ranged<Integer, Ranged<Character, Simple>>> triangle = businessParameters.forGroup(TRIANGLE);

    @Test
    public void testManualQuery(){

        Query<Ranged<Character, Ranged<Integer, Simple>>, String> query =
                new RangedQuery<>('A', Object::toString,
                        new RangedQuery<>(1, Object::toString,
                                new ValueQuery<>(PARAMETER_STRING)));

        System.out.println(query);
    }

    @Test
    public void testFluentQuery(){
        Query<Ranged<Character, Ranged<Integer, Simple>>, String> query =
                BasicQueries.<Character, Ranged<Integer, Simple>>
                        entryAt('A', Object::toString).andThen(entryAt(1, Object::toString)).andThen(valueOf(PARAMETER_STRING));

        Query<Ranged<Character, Ranged<Integer, Simple>>, String> query2 =
        BasicQueries.<Character, Ranged<Integer, Simple>>
        entryAt('A', Object::toString).andThen(
                BasicQueries.<Integer, Simple>
                entryAt(1, Object::toString).andThen(valueOf(PARAMETER_STRING)));

        System.out.println(query);
        System.out.println(query2);

    }

    @Test
    public void testFluentGroup(){
        Ranged<Character, Ranged<Integer, Simple>> group =
                ranged((Character c) -> c.toString(),
                        ranged((Integer i) -> i.toString(),
                                in(businessParameters.forGroup(TRIANGLE))))
                        .getEntryType();

        System.out.println(group);
        System.out.println(group.at('A').at(1).get(valueOf(PARAMETER_STRING)));
    }

    @Test
    public void testCustomGroup(){
        TVProgram tvProgram = TVProgram.using(businessParameters);
        tvProgram.forKey(Dag.MAANDAG).at(Slot.atHour(19)).getValue(PARAMETER_STRING);
        tvProgram.forKey(Dag.VRIJDAG);
    }

//
//    private static ParameterGroup<Simple> SIMPLE = newParameterGroup("SIMPLE", simple());
//    private static ParameterGroup<Mapped<Simple>> MAPPED = newParameterGroup("MAPPED", mapped());
//    private static ParameterGroup<Ranged<Integer, Simple>> RANGED = () -> "RANGED";
//    private static ParameterGroup<Ranged<Integer, Mapped<Simple>>> RANGED_MAPPED = newParameterGroup("RANGED_MAPPED", ranged(Integer.class, mapped()));
//    private static ParameterGroup<Mapped<Ranged<Integer, Simple>>> MAPPED_RANGED = newParameterGroup("MAPPED_RANGED", mapped(ranged(Integer.class)));
    private static ParameterGroup<Ranged<Character, Ranged<Integer, Simple>>> TRIANGLE = () -> "TRIANGLE";
//
    private static Parameter<String> PARAMETER_STRING = () -> "PString";
    private static Parameter<Integer> PARAMETER_INTEGER = () -> "PInteger";

//    private static final <ET extends EntryType> ParameterGroup<ET> newParameterGroup(String name, Function<BusinessParameterGroup<ET>, ET> entryType){
//        return new ParameterGroup<ET>() {
//            @Override
//            public String getName() {
//                return name;
//            }
//
//        };
//    }

}
