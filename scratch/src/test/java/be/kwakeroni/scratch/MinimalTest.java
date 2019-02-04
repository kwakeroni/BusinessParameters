package be.kwakeroni.scratch;

import be.kwakeroni.parameters.basic.client.query.EntryQuery;
import be.kwakeroni.parameters.basic.client.query.MappedQuery;
import be.kwakeroni.parameters.basic.client.query.RangedQuery;
import be.kwakeroni.parameters.basic.client.query.ValueQuery;
import be.kwakeroni.parameters.basic.type.Range;
import be.kwakeroni.parameters.client.api.model.Entry;
import be.kwakeroni.parameters.types.support.ParameterTypes;
import be.kwakeroni.scratch.env.Environment;
import be.kwakeroni.scratch.env.TestData;
import be.kwakeroni.scratch.tv.AbstractMappedRangedTVGroup;
import be.kwakeroni.scratch.tv.AbstractRangedTVGroup;
import be.kwakeroni.scratch.tv.Dag;
import be.kwakeroni.scratch.tv.HistoricizedTVGroup;
import be.kwakeroni.scratch.tv.MappedRangedFilterTVGroup;
import be.kwakeroni.scratch.tv.MappedRangedQueryTVGroup;
import be.kwakeroni.scratch.tv.MappedTVGroup;
import be.kwakeroni.scratch.tv.RangedFilterTVGroup;
import be.kwakeroni.scratch.tv.RangedQueryTVGroup;
import be.kwakeroni.scratch.tv.SimpleTVGroup;
import be.kwakeroni.scratch.tv.Slot;
import be.kwakeroni.test.logging.SLF4JTestLog;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.slf4j.event.Level;

import java.time.LocalDate;
import java.util.function.Supplier;

import static org.junit.Assert.assertEquals;

/**
 * (C) 2016 Maarten Van Puymbroeck
 */
@RunWith(Parameterized.class)
public class MinimalTest {
    static {
        SLF4JTestLog.setDisplayLevel(Level.DEBUG);
    }

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
    public void testHistoricizedValueQuery() {
        environment.runTestForGroup(HistoricizedTVGroup.instance());
        String program = environment.getBusinessParameters().get(HistoricizedTVGroup.instance(),
                new RangedQuery<>(LocalDate.of(2019, 8, 29), ParameterTypes.LOCAL_DATE, new ValueQuery<>(HistoricizedTVGroup.PROGRAM))).get();
        assertEquals("Summer Standup Show", program);
    }

    @Test
    public void testHistoricizedEntryQuery() {
        environment.runTestForGroup(HistoricizedTVGroup.instance());
        Entry entry = environment.getBusinessParameters().get(HistoricizedTVGroup.instance(),
                new RangedQuery<>(LocalDate.of(2019, 2, 28), ParameterTypes.LOCAL_DATE, new EntryQuery())
        ).get();

        assertEquals(LocalDate.of(2018, 12, 15), entry.getValue(HistoricizedTVGroup.PERIOD).getFrom());
        assertEquals(LocalDate.of(2019, 3, 15), entry.getValue(HistoricizedTVGroup.PERIOD).getTo());
        assertEquals("Winter Wonderland", entry.getValue(HistoricizedTVGroup.PROGRAM));
    }

    @Test
    public void testRangedValueQueryFilter() {
        testRangedValueQuery(RangedFilterTVGroup.instance());
    }

    @Test
    public void testRangedValueQueryQuery() {
        testRangedValueQuery(RangedQueryTVGroup.instance());
    }

    public void testRangedValueQuery(AbstractRangedTVGroup rangedGroup) {
        environment.runTestForGroup(rangedGroup);
        String program = environment.getBusinessParameters().get(rangedGroup,
                new RangedQuery<>(Slot.atHalfPast(9), Slot.type,
                        new ValueQuery<>(AbstractRangedTVGroup.PROGRAM))).get();
        assertEquals("Samson", program);
    }

    @Test
    public void testRangedEntryQueryFilter() {
        testRangedEntryQuery(RangedFilterTVGroup.instance());
    }

    @Test
    public void testRangedEntryQueryQuery() {
        testRangedEntryQuery(RangedQueryTVGroup.instance());
    }

    public void testRangedEntryQuery(AbstractRangedTVGroup rangedGroup) {
        environment.runTestForGroup(rangedGroup);
        Entry entry = environment.getBusinessParameters().get(rangedGroup,
                new RangedQuery<>(Slot.atHour(21), Slot.type,
                        new EntryQuery())).get();
        assertEquals(Range.of(Slot.atHalfPast(20), Slot.atHour(22)), entry.getValue(AbstractRangedTVGroup.SLOT));
        assertEquals("Morgen Maandag", entry.getValue(AbstractRangedTVGroup.PROGRAM));
    }

    @Test
    public void testMappedRangedValueQueryFilter() {
        testMappedRangedValueQuery(MappedRangedFilterTVGroup.instance());
    }

    @Test
    public void testMappedRangedValueQueryQuery() {
        testMappedRangedValueQuery(MappedRangedQueryTVGroup.instance());
    }

    public void testMappedRangedValueQuery(AbstractMappedRangedTVGroup rangedGroup) {
        environment.runTestForGroup(rangedGroup);

        assertEquals("Samson", getMappedRangedProgram(rangedGroup, Dag.ZATERDAG, Slot.atHalfPast(11)));
        assertEquals("Koers", getMappedRangedProgram(rangedGroup, Dag.ZATERDAG, Slot.atHour(14)));
        assertEquals("Morgen Maandag", getMappedRangedProgram(rangedGroup, Dag.ZONDAG, Slot.atHour(21)));
        assertEquals("Gisteren Zondag", getMappedRangedProgram(rangedGroup, Dag.MAANDAG, Slot.atHour(21)));
    }

    private String getMappedRangedProgram(AbstractMappedRangedTVGroup rangedGroup, Dag day, Slot slot) {
        return environment.getBusinessParameters().get(rangedGroup,
                new MappedQuery<>(day, Dag.type,
                        new RangedQuery<>(slot, Slot.type,
                                new ValueQuery<>(AbstractMappedRangedTVGroup.PROGRAM)))).get();
    }

}
