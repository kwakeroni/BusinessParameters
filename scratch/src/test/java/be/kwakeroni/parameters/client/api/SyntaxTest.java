package be.kwakeroni.parameters.client.api;

import be.kwakeroni.parameters.client.model.EntryType;
import be.kwakeroni.parameters.client.model.Parameter;
import be.kwakeroni.parameters.client.model.ParameterGroup;
import be.kwakeroni.parameters.client.model.basic.Mapped;
import be.kwakeroni.parameters.client.model.basic.Ranged;
import be.kwakeroni.parameters.client.model.basic.Simple;
import org.junit.Test;

import java.util.function.Function;

import static be.kwakeroni.parameters.client.query.basic.BasicQueryBuilder.*;
import static be.kwakeroni.parameters.client.model.basic.BasicEntryType.*;

public class SyntaxTest {

    private BusinessParameters businessParameters = new BusinessParameters() {
        @Override
        public <ET extends EntryType, T> T get(ParameterGroup<ET> group, Query<ET, T> query) {
            System.out.println(group.getName()+"."+query);
            return null;
        }

        @Override
        public <ET extends EntryType, T> T getValue(ParameterGroup<ET> group, Parameter<T> parameter, EntryIdentifier<ET> identifier) {
            return null;
        }

        @Override
        public <ET extends EntryType> Entry getEntry(ParameterGroup<ET> group, EntryIdentifier<ET> identifier) {
            return null;
        }
    };
    private BusinessParameterGroup<Simple> simple = businessParameters.forGroup(SIMPLE);
    private BusinessParameterGroup<Ranged<Integer, Simple>> ranged = businessParameters.forGroup(RANGED);
    private BusinessParameterGroup<Ranged<Integer, Ranged<Character, Simple>>> triangle = businessParameters.forGroup(TRIANGLE);

    @Test
    public void test(){

        String value = simple.get(valueOf(PARAMETER_STRING));
        Entry simpleValues = simple.get(entry());

        String valueAt1 = ranged.get(valueOf(PARAMETER_STRING).at(1));
        Entry entryAt7 = ranged.get(entry().at(7));

        String B2 = triangle.get(valueOf(PARAMETER_STRING).at('B').at(2));

    }

    @Test
    public void testReverse(){

        String value = simple.retrieve().value(PARAMETER_STRING);
        Entry simpleValues = simple.retrieve().entry();

        String valueAt1 = ranged.retrieve().at(7).value(PARAMETER_STRING);

        String B2 = triangle.retrieve().at(2).at('B').value(PARAMETER_STRING);

    }

    private static ParameterGroup<Simple> SIMPLE = newParameterGroup("SIMPLE", simple());
    private static ParameterGroup<Mapped<Simple>> MAPPED = newParameterGroup("MAPPED", mapped());
    private static ParameterGroup<Ranged<Integer, Simple>> RANGED = newParameterGroup("RANGED", ranged(Integer.class));
    private static ParameterGroup<Ranged<Integer, Mapped<Simple>>> RANGED_MAPPED = newParameterGroup("RANGED_MAPPED", ranged(Integer.class, mapped()));
    private static ParameterGroup<Mapped<Ranged<Integer, Simple>>> MAPPED_RANGED = newParameterGroup("MAPPED_RANGED", mapped(ranged(Integer.class)));
    private static ParameterGroup<Ranged<Integer, Ranged<Character, Simple>>> TRIANGLE = null;

    private static Parameter<String> PARAMETER_STRING = () -> "PString";
    private static Parameter<Integer> PARAMETER_INTEGER = () -> "PInteger";

    private static final <ET extends EntryType> ParameterGroup<ET> newParameterGroup(String name, Function<BusinessParameterGroup<ET>, ET> entryType){
        return new ParameterGroup<ET>() {
            @Override
            public String getName() {
                return name;
            }

            @Override
            public ET retrieve() {
                return entryType.apply(this);
            }
        };
    }

}
