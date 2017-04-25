package be.kwakeroni.scratch;

import be.kwakeroni.parameters.basic.client.query.EntryQuery;
import be.kwakeroni.parameters.basic.client.query.MappedQuery;
import be.kwakeroni.parameters.basic.client.query.RangedQuery;
import be.kwakeroni.parameters.basic.client.query.ValueQuery;
import be.kwakeroni.parameters.basic.type.Range;
import be.kwakeroni.parameters.client.api.model.Entry;
import be.kwakeroni.scratch.tv.*;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.function.Supplier;

import static org.junit.Assert.assertEquals;

/**
 * (C) 2016 Maarten Van Puymbroeck
 */
@RunWith(Parameterized.class)
public class MinimalTest {

    @Rule
    public Environment environment;

    public MinimalTest(TestMatrix.TestParameter<Supplier<TestData>> testDataSupplier) {
        this.environment = new Environment(testDataSupplier.value);
    }

    @Parameterized.Parameters(name = "{0}")
    public static Object[][] data() {
        return TestMatrix.backends().toParameterArray();
    }

    @Test
    public void testSimpleValueQuery() {
        environment.runTestForGroup(SimpleTVGroup.instance());
        Dag dag = environment.getBusinessParameters().get(SimpleTVGroup.instance(),
                new ValueQuery<>(SimpleTVGroup.DAY)).get();
        assertEquals(Dag.MAANDAG, dag);
    }

    @Test
    public void testSimpleEntryQuery() {
        environment.runTestForGroup(SimpleTVGroup.instance());
        Entry entry = environment.getBusinessParameters().get(SimpleTVGroup.instance(), new EntryQuery()).get();
        assertEquals(Dag.MAANDAG, entry.getValue(SimpleTVGroup.DAY));
        assertEquals(Slot.atHour(20), entry.getValue(SimpleTVGroup.SLOT));
    }

    @Test
    public void testMappedValueQuery() {
        environment.runTestForGroup(MappedTVGroup.instance());
        String program = environment.getBusinessParameters().get(MappedTVGroup.instance(),
                new MappedQuery<>(Dag.ZATERDAG, Dag.type,
                        new ValueQuery<>(MappedTVGroup.PROGRAM))).get();
        assertEquals("Samson", program);
    }

    @Test
    public void testMappedEntryQuery() {
        environment.runTestForGroup(MappedTVGroup.instance());
        Entry entry = environment.getBusinessParameters().get(MappedTVGroup.instance(),
                new MappedQuery<>(Dag.ZONDAG, Dag.type,
                        new EntryQuery())).get();
        assertEquals(Dag.ZONDAG, entry.getValue(MappedTVGroup.DAY));
        assertEquals("Morgen Maandag", entry.getValue(MappedTVGroup.PROGRAM));
    }

    @Test
    public void testRangedValueQuery() {
        environment.runTestForGroup(RangedTVGroup.instance());
        String program = environment.getBusinessParameters().get(RangedTVGroup.instance(),
                new RangedQuery<>(Slot.atHalfPast(9), Slot.type,
                        new ValueQuery<>(RangedTVGroup.PROGRAM))).get();
        assertEquals("Samson", program);
    }

    @Test
    public void testRangedEntryQuery() {
        environment.runTestForGroup(RangedTVGroup.instance());
        Entry entry = environment.getBusinessParameters().get(RangedTVGroup.instance(),
                new RangedQuery<>(Slot.atHour(21), Slot.type,
                        new EntryQuery())).get();
        assertEquals(Range.of(Slot.atHalfPast(20), Slot.atHour(22)), entry.getValue(RangedTVGroup.SLOT));
        assertEquals("Morgen Maandag", entry.getValue(RangedTVGroup.PROGRAM));
    }

    @Test
    public void testMappedRangedValueQuery() {
        environment.runTestForGroup(MappedRangedTVGroup.instance());

        assertEquals("Samson", getMappedRangedProgram(Dag.ZATERDAG, Slot.atHalfPast(11)));
        assertEquals("Koers", getMappedRangedProgram(Dag.ZATERDAG, Slot.atHour(14)));
        assertEquals("Morgen Maandag", getMappedRangedProgram(Dag.ZONDAG, Slot.atHour(21)));
        assertEquals("Gisteren Zondag", getMappedRangedProgram(Dag.MAANDAG, Slot.atHour(21)));
    }

    private String getMappedRangedProgram(Dag day, Slot slot) {
        return environment.getBusinessParameters().get(MappedRangedTVGroup.instance(),
                new MappedQuery<>(day, Dag.type,
                        new RangedQuery<>(slot, Slot.type,
                                new ValueQuery<>(MappedRangedTVGroup.PROGRAM)))).get();
    }

}
