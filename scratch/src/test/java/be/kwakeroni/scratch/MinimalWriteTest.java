package be.kwakeroni.scratch;

import be.kwakeroni.parameters.basic.client.model.Mapped;
import be.kwakeroni.parameters.basic.client.model.Ranged;
import be.kwakeroni.parameters.basic.client.model.Simple;
import be.kwakeroni.parameters.basic.client.query.EntryQuery;
import be.kwakeroni.parameters.basic.client.query.MappedQuery;
import be.kwakeroni.parameters.basic.client.query.RangedQuery;
import be.kwakeroni.parameters.basic.client.query.ValueQuery;
import be.kwakeroni.parameters.basic.type.Range;
import be.kwakeroni.parameters.client.api.model.Entry;
import be.kwakeroni.parameters.client.api.model.EntryType;
import be.kwakeroni.parameters.client.api.model.Parameter;
import be.kwakeroni.parameters.client.api.model.ParameterGroup;
import be.kwakeroni.parameters.client.api.query.Query;
import be.kwakeroni.scratch.env.Environment;
import be.kwakeroni.scratch.env.TestData;
import be.kwakeroni.scratch.tv.*;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.assertEquals;

/**
 * (C) 2017 Maarten Van Puymbroeck
 */
@RunWith(Parameterized.class)
public class MinimalWriteTest {

    private Logger LOG = LoggerFactory.getLogger(MinimalWriteTest.class);

    @Rule
    public Environment environment;
    @Rule
    public TestRule resetter;

    public MinimalWriteTest(TestMatrix.TestParameter<Supplier<TestData>> testDataSupplier) {
        this.environment = new Environment(testDataSupplier.value);
        resetter = this.environment.reset();
    }

    @Parameterized.Parameters(name = "{0}")
    public static Object[][] data() {
        return TestMatrix.backends().toParameterArray();
    }


    @Test
    public void testWriteSimpleValue() throws Exception {
        Query<Simple, Dag> query = new ValueQuery<>(SimpleTVGroup.DAY);

        Dag original = environment.getBusinessParameters().get(SimpleTVGroup.instance(), query).get();
        assertEquals(Dag.MAANDAG, original);

        environment.getWritableBusinessParameters().set(SimpleTVGroup.instance(), query, Dag.DONDERDAG);

        Dag modified = environment.getBusinessParameters().get(SimpleTVGroup.instance(), query).get();
        assertEquals(Dag.DONDERDAG, modified);
    }

    @Test
    public void testWriteSimpleEntry() throws Exception {
        Query<Simple, Entry> query = new EntryQuery();

        Entry entry = environment.getBusinessParameters().get(SimpleTVGroup.instance(), query).get();
        assertEquals(Dag.MAANDAG, entry.getValue(SimpleTVGroup.DAY));
        assertEquals(Slot.atHour(20), entry.getValue(SimpleTVGroup.SLOT));

        environment.getWritableBusinessParameters().set(SimpleTVGroup.instance(), query, SimpleTVGroup.entry(Dag.DONDERDAG, Slot.atHalfPast(18)));

        Entry modified = environment.getBusinessParameters().get(SimpleTVGroup.instance(), query).get();
        assertEquals(Dag.DONDERDAG, modified.getValue(SimpleTVGroup.DAY));
        assertEquals(Slot.atHalfPast(18), modified.getValue(SimpleTVGroup.SLOT));
    }

    @Test
    public void testWriteMappedValue() {
        Query<Mapped<Dag, Simple>, String> query =
                new MappedQuery<>(Dag.ZATERDAG, Dag.type,
                        new ValueQuery<>(MappedTVGroup.PROGRAM));

        String program = environment.getBusinessParameters().get(MappedTVGroup.instance(), query).get();
        assertEquals("Samson", program);

        environment.getWritableBusinessParameters().set(MappedTVGroup.instance(), query, "TikTak");

        String modified = environment.getBusinessParameters().get(MappedTVGroup.instance(), query).get();
        assertEquals("TikTak", modified);
    }

    @Test
    public void testWriteMappedEntry() {
        Query<Mapped<Dag, Simple>, Entry> query =
                new MappedQuery<>(Dag.ZONDAG, Dag.type,
                        new EntryQuery());

        Entry entry = environment.getBusinessParameters().get(MappedTVGroup.instance(), query).get();
        assertEquals(Dag.ZONDAG, entry.getValue(MappedTVGroup.DAY));
        assertEquals("Morgen Maandag", entry.getValue(MappedTVGroup.PROGRAM));

        environment.getWritableBusinessParameters().set(MappedTVGroup.instance(), query, MappedTVGroup.entry(Dag.DONDERDAG, "Alles Kan Beter"));

        assertThat(environment.getBusinessParameters().get(MappedTVGroup.instance(), query)).isEmpty();


        Entry modified = environment.getBusinessParameters().get(MappedTVGroup.instance(),
                new MappedQuery<>(Dag.DONDERDAG, Dag.type, new EntryQuery())).get();
        assertEquals(Dag.DONDERDAG, modified.getValue(MappedTVGroup.DAY));
        assertEquals("Alles Kan Beter", modified.getValue(MappedTVGroup.PROGRAM));

    }

    @Test
    public void testWriteRangedFilterValue() {
        testWriteRangedValue(RangedFilterTVGroup.instance());
    }

    @Test
    public void testWriteRangedQueryValue() {
        testWriteRangedValue(RangedQueryTVGroup.instance());
    }

    public void testWriteRangedValue(AbstractRangedTVGroup rangedGroup) {
        Query<Ranged<Slot, Simple>, String> query = new RangedQuery<>(Slot.atHalfPast(9), Slot.type,
                new ValueQuery<>(AbstractRangedTVGroup.PROGRAM));

        String original = environment.getBusinessParameters().get(rangedGroup, query).get();
        assertEquals("Samson", original);

        environment.getWritableBusinessParameters().set(rangedGroup, query, "TikTak");

        String modified = environment.getBusinessParameters().get(rangedGroup, query).get();
        assertEquals("TikTak", modified);

    }


    @Test
    public void testWriteRangedFilterEntry() {
        testWriteRangedEntry(RangedFilterTVGroup.instance());
    }

    @Test
    public void testWriteRangedQueryEntry() {
        testWriteRangedEntry(RangedQueryTVGroup.instance());
    }

    public void testWriteRangedEntry(AbstractRangedTVGroup rangedGroup) {
        Query<Ranged<Slot, Simple>, Entry> query = new RangedQuery<>(Slot.atHalfPast(9), Slot.type,
                new EntryQuery());

        Entry entry = get(rangedGroup, query).get();
        assertEquals(Range.of(Slot.atHour(8), Slot.atHour(12)), entry.getValue(AbstractRangedTVGroup.SLOT));
        assertEquals("Samson", entry.getValue(AbstractRangedTVGroup.PROGRAM));

        environment.getWritableBusinessParameters().set(rangedGroup, query, AbstractRangedTVGroup.entry(Slot.atHour(10), Slot.atHour(12), "TikTak"));

        assertThat(get(rangedGroup, query)).isEmpty();

        Entry modified = get(rangedGroup, new RangedQuery<>(Slot.atHour(10), Slot.type, new EntryQuery())).get();
        assertEquals(Range.of(Slot.atHour(10), Slot.atHour(12)), modified.getValue(AbstractRangedTVGroup.SLOT));
        assertEquals("TikTak", modified.getValue(MappedTVGroup.PROGRAM));
    }

    @Test
    public void testAddMappedEntry() {
        assertThat(get(MappedTVGroup.instance(), MappedTVGroup.programQuery(Dag.DINSDAG))).isEmpty();

        Entry entry = MappedTVGroup.entry(Dag.DINSDAG, "Panorama");
        environment.getWritableBusinessParameters().addEntry(MappedTVGroup.instance(), entry);

        assertEquals("Panorama", get(Dag.DINSDAG));
    }

    @Test
    public void testAddRangedFilterEntry() {
        testAddRangedEntry(RangedFilterTVGroup.instance());
    }

    @Test
    public void testAddRangedQueryEntry() {
        testAddRangedEntry(RangedQueryTVGroup.instance());
    }

    public void testAddRangedEntry(AbstractRangedTVGroup rangedGroup) {
        assertThat(get(rangedGroup, AbstractRangedTVGroup.programQuery(Slot.atHalfPast(3)))).isEmpty();

        Entry entry = AbstractRangedTVGroup.entry(Slot.atHour(3), Slot.atHour(4), "TestBeeld");
        environment.getWritableBusinessParameters().addEntry(rangedGroup, entry);

        assertEquals("TestBeeld", get(rangedGroup, Slot.atHalfPast(3)));
    }

    @Test
    public void testAddSimpleEntry() {
        Query<Simple, Dag> query = new ValueQuery<>(SimpleTVGroup.DAY);

        Dag original = environment.getBusinessParameters().get(SimpleTVGroup.instance(), query).get();
        assertEquals(Dag.MAANDAG, original);

        assertThatThrownBy(() ->
                environment.getWritableBusinessParameters().addEntry(SimpleTVGroup.instance(), SimpleTVGroup.entry(Dag.WOENSDAG, Slot.atHour(7)))
        ).isInstanceOf(IllegalStateException.class);

        Dag modified = environment.getBusinessParameters().get(SimpleTVGroup.instance(), query).get();
        assertEquals(Dag.MAANDAG, modified);
    }

    @Test
    public void testAddIncompleteEntry() {
        assertThat(get(MappedTVGroup.instance(), MappedTVGroup.programQuery(Dag.DINSDAG))).isEmpty();

        Map<String, String> map = MappedTVGroup.entry(Dag.DINSDAG, "Panorama").toMap();
        map.remove(MappedTVGroup.PROGRAM.getName());
        map.put("test", "value");
        Entry entry = new DefaultEntry(map);

        assertThatThrownBy(() ->
                environment.getWritableBusinessParameters().addEntry(MappedTVGroup.instance(), entry)
        ).isInstanceOf(IllegalArgumentException.class)
                .satisfies(throwable -> LOG.error(throwable.toString()))
                .hasMessageContaining("missing: [program]")
                .hasMessageContaining("unexpected: [test]");

    }

    @Test
    public void testAddDuplicateMapKey() {
        {
            String program = environment.getBusinessParameters().get(MappedTVGroup.instance(), MappedTVGroup.programQuery(Dag.ZONDAG)).get();
            assertEquals("Morgen Maandag", program);
        }

        assertThatThrownBy(() ->
                environment.getWritableBusinessParameters().addEntry(MappedTVGroup.instance(), MappedTVGroup.entry(Dag.ZONDAG, "De Zevende Dag"))
        ).isInstanceOf(IllegalStateException.class)
                .satisfies(throwable -> LOG.error(throwable.toString()))
                .hasMessageContaining("day=ZONDAG");

        {
            String program = environment.getBusinessParameters().get(MappedTVGroup.instance(), MappedTVGroup.programQuery(Dag.ZONDAG)).get();
            assertEquals("Morgen Maandag", program);
        }
    }

    @Test
    public void testModifyEntryToDuplicateMapKey() {
        {
            assertEquals("Morgen Maandag", get(Dag.ZONDAG));
            assertEquals("Samson", get(Dag.ZATERDAG));
        }


        assertThatThrownBy(() ->
                environment.getWritableBusinessParameters().set(
                        MappedTVGroup.instance(),
                        MappedTVGroup.entryQuery(Dag.ZATERDAG),
                        MappedTVGroup.entry(Dag.ZONDAG, "Samson"))
        ).isInstanceOf(IllegalStateException.class)
                .satisfies(throwable -> LOG.error(throwable.toString()))
                .hasMessageContaining("day=ZONDAG");

        {
            assertEquals("Morgen Maandag", get(Dag.ZONDAG));
            assertEquals("Samson", get(Dag.ZATERDAG));
        }
    }

    @Test
    public void testModifyValueToDuplicateMapKey() {
        {
            assertEquals("Morgen Maandag", get(Dag.ZONDAG));
            assertEquals("Samson", get(Dag.ZATERDAG));
        }


        assertThatThrownBy(() ->
                environment.getWritableBusinessParameters().set(
                        MappedTVGroup.instance(),
                        MappedTVGroup.valueQuery(Dag.ZONDAG, MappedTVGroup.DAY),
                        Dag.ZATERDAG)
        ).isInstanceOf(IllegalStateException.class)
                .satisfies(throwable -> LOG.error(throwable.toString()))
                .hasMessageContaining("day=ZATERDAG");

        {
            assertEquals("Morgen Maandag", get(Dag.ZONDAG));
            assertEquals("Samson", get(Dag.ZATERDAG));
        }
    }


    @Test
    public void testAddOverlappingRangeFilter() {
        testAddOverlappingRange(RangedFilterTVGroup.instance());
    }

    @Test
    public void testAddOverlappingRangeQuery() {
        testAddOverlappingRange(RangedQueryTVGroup.instance());
    }

    public void testAddOverlappingRange(AbstractRangedTVGroup rangedGroup) {
        {
            assertEquals("Samson", get(rangedGroup, Slot.atHour(8)));
            assertEquals("Samson", get(rangedGroup, Slot.atHalfPast(11)));
        }

        assertThatThrownBy(() ->
                environment.getWritableBusinessParameters().addEntry(rangedGroup, AbstractRangedTVGroup.entry(Slot.atHour(7), Slot.atHalfPast(8), "De Zevende Dag"))
        ).isInstanceOf(IllegalStateException.class)
                .satisfies(throwable -> LOG.error(throwable.toString()))
                .hasMessageContaining("slot");

        assertThatThrownBy(() ->
                environment.getWritableBusinessParameters().addEntry(rangedGroup, AbstractRangedTVGroup.entry(Slot.atHalfPast(11), Slot.atHour(12), "De Zevende Dag"))
        ).isInstanceOf(IllegalStateException.class)
                .satisfies(throwable -> LOG.error(throwable.toString()))
                .hasMessageContaining("slot");

        {
            assertEquals("Samson", get(rangedGroup, Slot.atHour(8)));
            assertEquals("Samson", get(rangedGroup, Slot.atHalfPast(11)));
        }
    }

    @Test
    public void testAddFullyContainedOverlappingRangeFilter() {
        testAddFullyContainedOverlappingRange(RangedFilterTVGroup.instance());
    }

    @Test
    public void testAddFullyContainedOverlappingRangeQuery() {
        testAddFullyContainedOverlappingRange(RangedQueryTVGroup.instance());
    }

    public void testAddFullyContainedOverlappingRange(AbstractRangedTVGroup rangedGroup) {
        {
            assertEquals("Samson", get(rangedGroup, Slot.atHour(8)));
            assertEquals("Samson", get(rangedGroup, Slot.atHalfPast(11)));
        }

        assertThatThrownBy(() ->
                environment.getWritableBusinessParameters().addEntry(rangedGroup, AbstractRangedTVGroup.entry(Slot.atHour(9), Slot.atHalfPast(10), "De Zevende Dag"))
        ).isInstanceOf(IllegalStateException.class)
                .satisfies(throwable -> LOG.error(throwable.toString()))
                .hasMessageContaining("slot");

        {
            assertEquals("Samson", get(rangedGroup, Slot.atHour(8)));
            assertEquals("Samson", get(rangedGroup, Slot.atHalfPast(10)));
        }
    }

    @Test
    public void testAddAdjoiningRangeFilter() {
        testAddAdjoiningRange(RangedFilterTVGroup.instance());
    }

    @Test
    public void testAddAdjoiningRangeQuery() {
        testAddAdjoiningRange(RangedQueryTVGroup.instance());
    }

    public void testAddAdjoiningRange(AbstractRangedTVGroup rangedGroup) {
        {
            assertEquals("Samson", get(rangedGroup, Slot.atHour(8)));
            assertEquals("Samson", get(rangedGroup, Slot.atHalfPast(11)));
        }

        environment.getWritableBusinessParameters().addEntry(rangedGroup,
                AbstractRangedTVGroup.entry(Slot.atHour(7), Slot.atHour(8), "Het Nieuws"));
        environment.getWritableBusinessParameters().addEntry(rangedGroup,
                AbstractRangedTVGroup.entry(Slot.atHour(12), Slot.atHour(13), "Het Journaal"));

        {
            assertEquals("Het Nieuws", get(rangedGroup, Slot.atHour(7)));
            assertEquals("Samson", get(rangedGroup, Slot.atHour(8)));
            assertEquals("Het Journaal", get(rangedGroup, Slot.atHour(12)));
            assertNoEntry(rangedGroup, Slot.atHour(13));
        }

    }

    @Test
    public void testAddEntryForRangeInAnotherMapKeyFilter() {
        testAddEntryForRangeInAnotherMapKey(MappedRangedFilterTVGroup.instance());
    }

    @Test
    public void testAddEntryForRangeInAnotherMapKeyQuery() {
        testAddEntryForRangeInAnotherMapKey(MappedRangedQueryTVGroup.instance());
    }

    public void testAddEntryForRangeInAnotherMapKey(AbstractMappedRangedTVGroup rangedGroup) {
        assertEquals("Samson", get(Dag.ZATERDAG, rangedGroup, Slot.atHour(10)));

        environment.getWritableBusinessParameters().addEntry(rangedGroup,
                AbstractMappedRangedTVGroup.entry(Dag.DINSDAG, Slot.atHour(9), Slot.atHour(11), "Belspelletje"));

        assertEquals("Samson", get(Dag.ZATERDAG, rangedGroup, Slot.atHour(10)));
        assertEquals("Belspelletje", get(Dag.DINSDAG, rangedGroup, Slot.atHour(10)));

    }

    @Test
    public void testAddEntryForOtherRangeInSameMapKeyFilter() {
        testAddEntryForOtherRangeInSameMapKey(MappedRangedFilterTVGroup.instance());
    }

    @Test
    public void testAddEntryForOtherRangeInSameMapKeyQuery() {
        testAddEntryForOtherRangeInSameMapKey(MappedRangedQueryTVGroup.instance());
    }

    public void testAddEntryForOtherRangeInSameMapKey(AbstractMappedRangedTVGroup rangedGroup) {
        assertEquals("Morgen Maandag", get(Dag.ZONDAG, rangedGroup, Slot.atHour(21)));

        environment.getWritableBusinessParameters().addEntry(rangedGroup,
                AbstractMappedRangedTVGroup.entry(Dag.ZONDAG, Slot.atHour(19), Slot.atHour(20), "Zondag Josdag"));

        assertEquals("Morgen Maandag", get(Dag.ZONDAG, rangedGroup, Slot.atHour(21)));
        assertEquals("Zondag Josdag", get(Dag.ZONDAG, rangedGroup, Slot.atHour(19)));

    }

    @Test
    public void testModifyRangeToOverlappingRangeFilter() {
        testModifyRangeToOverlappingRange(MappedRangedFilterTVGroup.instance());
    }

    @Test
    public void testModifyRangeToOverlappingRangeQuery() {
        testModifyRangeToOverlappingRange(MappedRangedQueryTVGroup.instance());
    }

    public void testModifyRangeToOverlappingRange(AbstractMappedRangedTVGroup rangedGroup) {
        assertEquals("Samson", get(Dag.ZATERDAG, rangedGroup, Slot.atHour(10)));
        assertEquals("Koers", get(Dag.ZATERDAG, rangedGroup, Slot.atHour(16)));

        assertThatThrownBy(() ->
                environment.getWritableBusinessParameters().set(rangedGroup,
                        AbstractMappedRangedTVGroup.valueQuery(Dag.ZATERDAG, Slot.atHour(16), AbstractMappedRangedTVGroup.SLOT),
                        Range.of(Slot.atHour(11), Slot.atHour(18)))
        ).isInstanceOf(IllegalStateException.class)
                .satisfies(exc -> LOG.error(exc.getMessage()));

        assertEquals("Samson", get(Dag.ZATERDAG, rangedGroup, Slot.atHour(10)));
        assertEquals("Koers", get(Dag.ZATERDAG, rangedGroup, Slot.atHour(16)));

    }

    @Test
    public void testModifyKeyToOverlappingRangeFilter() {
        testModifyKeyToOverlappingRange(MappedRangedFilterTVGroup.instance());
    }

    @Test
    public void testModifyKeyToOverlappingRangeQuery() {
        testModifyKeyToOverlappingRange(MappedRangedQueryTVGroup.instance());
    }

    public void testModifyKeyToOverlappingRange(AbstractMappedRangedTVGroup rangedGroup) {
        assertEquals("Morgen Maandag", get(Dag.ZONDAG, rangedGroup, Slot.atHour(21)));
        assertEquals("Gisteren Zondag", get(Dag.MAANDAG, rangedGroup, Slot.atHour(21)));

        assertThatThrownBy(() ->
                environment.getWritableBusinessParameters().set(rangedGroup,
                        AbstractMappedRangedTVGroup.valueQuery(Dag.ZONDAG, Slot.atHour(21), AbstractMappedRangedTVGroup.DAY),
                        Dag.MAANDAG)
        ).isInstanceOf(IllegalStateException.class)
                .satisfies(exc -> LOG.error(exc.getMessage()));

        assertEquals("Morgen Maandag", get(Dag.ZONDAG, rangedGroup, Slot.atHour(21)));
        assertEquals("Gisteren Zondag", get(Dag.MAANDAG, rangedGroup, Slot.atHour(21)));

    }

    @Test
    public void testModifyKeyToNonOverlappingRangeFilter() {
        testModifyKeyToNonOverlappingRange(MappedRangedFilterTVGroup.instance());
    }

    @Test
    public void testModifyKeyToNonOverlappingRangeQuery() {
        testModifyKeyToNonOverlappingRange(MappedRangedQueryTVGroup.instance());
    }

    public void testModifyKeyToNonOverlappingRange(AbstractMappedRangedTVGroup rangedGroup) {
        assertEquals("Morgen Maandag", get(Dag.ZONDAG, rangedGroup, Slot.atHour(21)));

        environment.getWritableBusinessParameters().set(rangedGroup,
                AbstractMappedRangedTVGroup.valueQuery(Dag.ZONDAG, Slot.atHour(21), AbstractMappedRangedTVGroup.DAY),
                Dag.ZATERDAG);

        assertEquals("Morgen Maandag", get(Dag.ZATERDAG, rangedGroup, Slot.atHour(21)));

    }

    @Test
    public void testWriteNullValue() {
        Query<Simple, Dag> query = new ValueQuery<>(SimpleTVGroup.DAY);

        Dag original = environment.getBusinessParameters().get(SimpleTVGroup.instance(), query).get();
        assertEquals(Dag.MAANDAG, original);

        assertThatThrownBy(() ->
                environment.getWritableBusinessParameters().set(SimpleTVGroup.instance(), query, null)
        ).isInstanceOf(IllegalArgumentException.class)
                .satisfies(exc -> LOG.error(exc.getMessage()));

        Dag modified = environment.getBusinessParameters().get(SimpleTVGroup.instance(), query).get();
        assertEquals(Dag.MAANDAG, modified);
    }

    private void assertNoEntry(Dag dag) {
        assertThat(get(MappedTVGroup.instance(), MappedTVGroup.programQuery(dag))).isEmpty();
    }

    private void assertNoEntry(AbstractRangedTVGroup rangedGroup, Slot slot) {
        assertThat(get(rangedGroup, AbstractRangedTVGroup.programQuery(slot))).isEmpty();
    }

    private void assertNoEntry(AbstractMappedRangedTVGroup rangedGroup, Dag dag, Slot slot) {
        assertThat(get(rangedGroup, AbstractMappedRangedTVGroup.programQuery(dag, slot))).isEmpty();
    }

    private String get(Dag dag) {
        return get(MappedTVGroup.instance(), MappedTVGroup.programQuery(dag)).get();
    }

    private String get(AbstractRangedTVGroup rangedGroup, Slot slot) {
        return get(rangedGroup, AbstractRangedTVGroup.programQuery(slot)).get();
    }

    private String get(Dag dag, AbstractMappedRangedTVGroup rangedGroup, Slot slot) {
        return get(rangedGroup, AbstractMappedRangedTVGroup.programQuery(dag, slot)).get();
    }

    private <ET extends EntryType, T> Optional<T> get(ParameterGroup<ET> group, Query<ET, T> query) {
        return environment.getBusinessParameters().get(group, query);
    }


    class DefaultEntry implements Entry {

        private final Map<String, String> values;

        DefaultEntry(Map<String, String> values) {
            this.values = Collections.unmodifiableMap(values);
        }

        @Override
        public <T> T getValue(Parameter<T> parameter) {
            String value = this.values.get(parameter.getName());
            return (value == null) ? null : parameter.fromString(value);
        }

        @Override
        public boolean hasValue(Parameter<?> parameter) {
            return this.values.containsKey(parameter.getName());
        }

        @Override
        public Map<String, String> toMap() {
            return this.values;
        }
    }

}
