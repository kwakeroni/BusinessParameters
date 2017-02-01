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
import be.kwakeroni.scratch.tv.Dag;
import be.kwakeroni.scratch.tv.MappedRangedTVGroup;
import be.kwakeroni.scratch.tv.MappedTVGroup;
import be.kwakeroni.scratch.tv.RangedTVGroup;
import be.kwakeroni.scratch.tv.SimpleTVGroup;
import be.kwakeroni.scratch.tv.Slot;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.*;

/**
 * (C) 2017 Maarten Van Puymbroeck
 */
public class MinimalWriteTest {

    private Logger LOG = LoggerFactory.getLogger(MinimalWriteTest.class);

    Environment environment;

    @Before
    public void setUpEnvironment() {
        this.environment = new Environment();
    }

    @Test
    public void testWriteSimpleValue() {
        Query<Simple, Dag> query = new ValueQuery<>(SimpleTVGroup.DAY);

        Dag original = environment.getBusinessParameters().get(SimpleTVGroup.instance(), query);
        assertEquals(Dag.MAANDAG, original);

        environment.getWritableBusinessParameters().set(SimpleTVGroup.instance(), query, Dag.DONDERDAG);

        Dag modified = environment.getBusinessParameters().get(SimpleTVGroup.instance(), query);
        assertEquals(Dag.DONDERDAG, modified);
    }

    @Test
    public void testWriteSimpleEntry() {
        Query<Simple, Entry> query = new EntryQuery();

        Entry entry = environment.getBusinessParameters().get(SimpleTVGroup.instance(), query);
        assertEquals(Dag.MAANDAG, entry.getValue(SimpleTVGroup.DAY));
        assertEquals(Slot.atHour(20), entry.getValue(SimpleTVGroup.SLOT));

        environment.getWritableBusinessParameters().set(SimpleTVGroup.instance(), query, SimpleTVGroup.entry(Dag.DONDERDAG, Slot.atHalfPast(18)));

        Entry modified = environment.getBusinessParameters().get(SimpleTVGroup.instance(), query);
        assertEquals(Dag.DONDERDAG, modified.getValue(SimpleTVGroup.DAY));
        assertEquals(Slot.atHalfPast(18), modified.getValue(SimpleTVGroup.SLOT));
    }

    @Test
    public void testWriteMappedValue() {
        Query<Mapped<Dag, Simple>, String> query =
                new MappedQuery<>(Dag.ZATERDAG, Dag.type,
                        new ValueQuery<>(MappedTVGroup.PROGRAM));

        String program = environment.getBusinessParameters().get(MappedTVGroup.instance(), query);
        assertEquals("Samson", program);

        environment.getWritableBusinessParameters().set(MappedTVGroup.instance(), query, "TikTak");

        String modified = environment.getBusinessParameters().get(MappedTVGroup.instance(), query);
        assertEquals("TikTak", modified);
    }

    @Test
    public void testWriteMappedEntry() {
        Query<Mapped<Dag, Simple>, Entry> query =
                new MappedQuery<>(Dag.ZONDAG, Dag.type,
                        new EntryQuery());

        Entry entry = environment.getBusinessParameters().get(MappedTVGroup.instance(), query);
        assertEquals(Dag.ZONDAG, entry.getValue(MappedTVGroup.DAY));
        assertEquals("Morgen Maandag", entry.getValue(MappedTVGroup.PROGRAM));

        environment.getWritableBusinessParameters().set(MappedTVGroup.instance(), query, MappedTVGroup.entry(Dag.DONDERDAG, "Alles Kan Beter"));

        Entry old = environment.getBusinessParameters().get(MappedTVGroup.instance(), query);
        assertNull(old);

        Entry modified = environment.getBusinessParameters().get(MappedTVGroup.instance(),
                new MappedQuery<>(Dag.DONDERDAG, Dag.type, new EntryQuery()));
        assertEquals(Dag.DONDERDAG, modified.getValue(MappedTVGroup.DAY));
        assertEquals("Alles Kan Beter", modified.getValue(MappedTVGroup.PROGRAM));

    }

    @Test
    public void testWriteRangedValue() {
        Query<Ranged<Slot, Simple>, String> query = new RangedQuery<>(Slot.atHalfPast(9), Slot.type,
                new ValueQuery<>(RangedTVGroup.PROGRAM));

        String original = environment.getBusinessParameters().get(RangedTVGroup.instance(), query);
        assertEquals("Samson", original);

        environment.getWritableBusinessParameters().set(RangedTVGroup.instance(), query, "TikTak");

        String modified = environment.getBusinessParameters().get(RangedTVGroup.instance(), query);
        assertEquals("TikTak", modified);

    }


    @Test
    public void testWriteRangedEntry() {
        Query<Ranged<Slot, Simple>, Entry> query = new RangedQuery<>(Slot.atHalfPast(9), Slot.type,
                new EntryQuery());

        Entry entry = get(RangedTVGroup.instance(), query);
        assertEquals(Range.of(Slot.atHour(8), Slot.atHour(12)), entry.getValue(RangedTVGroup.SLOT));
        assertEquals("Samson", entry.getValue(RangedTVGroup.PROGRAM));

        environment.getWritableBusinessParameters().set(RangedTVGroup.instance(), query, RangedTVGroup.entry(Slot.atHour(10), Slot.atHour(12), "TikTak"));

        Entry old = get(RangedTVGroup.instance(), query);
        assertNull(old);

        Entry modified = get(RangedTVGroup.instance(), new RangedQuery<>(Slot.atHour(10), Slot.type, new EntryQuery()));
        assertEquals(Range.of(Slot.atHour(10), Slot.atHour(12)), modified.getValue(RangedTVGroup.SLOT));
        assertEquals("TikTak", modified.getValue(MappedTVGroup.PROGRAM));
    }

    @Test
    public void testAddMappedEntry() {
        assertNull(get(MappedTVGroup.instance(), MappedTVGroup.programQuery(Dag.DINSDAG)));

        Entry entry = MappedTVGroup.entry(Dag.DINSDAG, "Panorama");
        environment.getWritableBusinessParameters().addEntry(MappedTVGroup.instance(), entry);

        assertEquals("Panorama", get(MappedTVGroup.instance(), MappedTVGroup.programQuery(Dag.DINSDAG)));
    }

    @Test
    public void testAddRangedEntry() {
        assertNull(get(RangedTVGroup.instance(), RangedTVGroup.programQuery(Slot.atHalfPast(3))));

        Entry entry = RangedTVGroup.entry(Slot.atHour(3), Slot.atHour(4), "TestBeeld");
        environment.getWritableBusinessParameters().addEntry(RangedTVGroup.instance(), entry);

        assertEquals("TestBeeld", get(RangedTVGroup.instance(), RangedTVGroup.programQuery(Slot.atHalfPast(3))));
    }

    @Test
    public void testAddSimpleEntry() {
        Query<Simple, Dag> query = new ValueQuery<>(SimpleTVGroup.DAY);

        Dag original = environment.getBusinessParameters().get(SimpleTVGroup.instance(), query);
        assertEquals(Dag.MAANDAG, original);

        assertThatThrownBy(() ->
                environment.getWritableBusinessParameters().addEntry(SimpleTVGroup.instance(), SimpleTVGroup.entry(Dag.WOENSDAG, Slot.atHour(7)))
        ).isInstanceOf(IllegalStateException.class);

        Dag modified = environment.getBusinessParameters().get(SimpleTVGroup.instance(), query);
        assertEquals(Dag.MAANDAG, modified);
    }

    @Test
    public void testAddIncompleteEntry() {
        assertNull(get(MappedTVGroup.instance(), MappedTVGroup.programQuery(Dag.DINSDAG)));

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
            String program = environment.getBusinessParameters().get(MappedTVGroup.instance(), MappedTVGroup.programQuery(Dag.ZONDAG));
            assertEquals("Morgen Maandag", program);
        }

        assertThatThrownBy(() ->
                environment.getWritableBusinessParameters().addEntry(MappedTVGroup.instance(), MappedTVGroup.entry(Dag.ZONDAG, "De Zevende Dag"))
        ).isInstanceOf(IllegalStateException.class)
                .satisfies(throwable -> LOG.error(throwable.toString()))
                .hasMessageContaining("day=ZONDAG");

        {
            String program = environment.getBusinessParameters().get(MappedTVGroup.instance(), MappedTVGroup.programQuery(Dag.ZONDAG));
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
    public void testAddOverlappingRange() {
        {
            assertEquals("Samson", get(Slot.atHour(8)));
            assertEquals("Samson", get(Slot.atHalfPast(11)));
        }

        assertThatThrownBy(() ->
                environment.getWritableBusinessParameters().addEntry(RangedTVGroup.instance(), RangedTVGroup.entry(Slot.atHour(7), Slot.atHalfPast(8), "De Zevende Dag"))
        ).isInstanceOf(IllegalStateException.class)
                .satisfies(throwable -> LOG.error(throwable.toString()))
                .hasMessageContaining("slot");

        assertThatThrownBy(() ->
                environment.getWritableBusinessParameters().addEntry(RangedTVGroup.instance(), RangedTVGroup.entry(Slot.atHalfPast(11), Slot.atHour(12), "De Zevende Dag"))
        ).isInstanceOf(IllegalStateException.class)
                .satisfies(throwable -> LOG.error(throwable.toString()))
                .hasMessageContaining("slot");

        {
            assertEquals("Samson", get(Slot.atHour(8)));
            assertEquals("Samson", get(Slot.atHalfPast(11)));
        }
    }

    @Test
    public void testAddAdjoiningRange() {
        {
            assertEquals("Samson", get(Slot.atHour(8)));
            assertEquals("Samson", get(Slot.atHalfPast(11)));
        }

        environment.getWritableBusinessParameters().addEntry(RangedTVGroup.instance(),
                RangedTVGroup.entry(Slot.atHour(7), Slot.atHour(8), "Het Nieuws"));
        environment.getWritableBusinessParameters().addEntry(RangedTVGroup.instance(),
                RangedTVGroup.entry(Slot.atHour(12), Slot.atHour(13), "Het Journaal"));

        {
            assertEquals("Het Nieuws", get(Slot.atHour(7)));
            assertEquals("Samson", get(Slot.atHour(8)));
            assertEquals("Het Journaal", get(Slot.atHour(12)));
            assertNull(get(Slot.atHour(13)));
        }

    }

    @Test
    public void testAddEntryForRangeInAnotherMapKey(){
        assertEquals("Samson", get(Dag.ZATERDAG, Slot.atHour(10)));

        environment.getWritableBusinessParameters().addEntry(MappedRangedTVGroup.instance(),
                MappedRangedTVGroup.entry(Dag.DINSDAG, Slot.atHour(9), Slot.atHour(11), "Belspelletje"));

        assertEquals("Samson", get(Dag.ZATERDAG, Slot.atHour(10)));
        assertEquals("Belspelletje", get(Dag.DINSDAG, Slot.atHour(10)));

    }

    @Test
    public void testAddEntryForOtherRangeInSameMapKey(){
        assertEquals("Morgen Maandag", get(Dag.ZONDAG, Slot.atHour(21)));

        environment.getWritableBusinessParameters().addEntry(MappedRangedTVGroup.instance(),
                MappedRangedTVGroup.entry(Dag.ZONDAG, Slot.atHour(19), Slot.atHour(20), "Zondag Josdag"));

        assertEquals("Morgen Maandag", get(Dag.ZONDAG, Slot.atHour(21)));
        assertEquals("Zondag Josdag", get(Dag.ZONDAG, Slot.atHour(19)));

    }

    @Test
    public void testModifyRangeToOverlappingRange(){
        assertEquals("Samson", get(Dag.ZATERDAG, Slot.atHour(10)));
        assertEquals("Koers", get(Dag.ZATERDAG, Slot.atHour(16)));

        assertThatThrownBy(() ->
        environment.getWritableBusinessParameters().set(MappedRangedTVGroup.instance(),
                MappedRangedTVGroup.valueQuery(Dag.ZATERDAG, Slot.atHour(16), MappedRangedTVGroup.SLOT),
                Range.of(Slot.atHour(11), Slot.atHour(18)))
        ).isInstanceOf(IllegalStateException.class)
                .satisfies(exc -> LOG.error(exc.getMessage()));

        assertEquals("Samson", get(Dag.ZATERDAG, Slot.atHour(10)));
        assertEquals("Koers", get(Dag.ZATERDAG, Slot.atHour(16)));

    }

    @Test
    public void testModifyKeyToOverlappingRange(){
        assertEquals("Morgen Maandag", get(Dag.ZONDAG, Slot.atHour(21)));
        assertEquals("Gisteren Zondag", get(Dag.MAANDAG, Slot.atHour(21)));

        assertThatThrownBy(() ->
                environment.getWritableBusinessParameters().set(MappedRangedTVGroup.instance(),
                        MappedRangedTVGroup.valueQuery(Dag.ZONDAG, Slot.atHour(21), MappedRangedTVGroup.DAY),
                        Dag.MAANDAG)
        ).isInstanceOf(IllegalStateException.class)
                .satisfies(exc -> LOG.error(exc.getMessage()));

        assertEquals("Morgen Maandag", get(Dag.ZONDAG, Slot.atHour(21)));
        assertEquals("Gisteren Zondag", get(Dag.MAANDAG, Slot.atHour(21)));

    }

    @Test
    public void testModifyKeyToNonOverlappingRange(){
        assertEquals("Morgen Maandag", get(Dag.ZONDAG, Slot.atHour(21)));

                environment.getWritableBusinessParameters().set(MappedRangedTVGroup.instance(),
                        MappedRangedTVGroup.valueQuery(Dag.ZONDAG, Slot.atHour(21), MappedRangedTVGroup.DAY),
                        Dag.ZATERDAG);

        assertEquals("Morgen Maandag", get(Dag.ZATERDAG, Slot.atHour(21)));

    }

    @Test
    public void testWriteNullValue() {
        Query<Simple, Dag> query = new ValueQuery<>(SimpleTVGroup.DAY);

        Dag original = environment.getBusinessParameters().get(SimpleTVGroup.instance(), query);
        assertEquals(Dag.MAANDAG, original);

        assertThatThrownBy(() ->
            environment.getWritableBusinessParameters().set(SimpleTVGroup.instance(), query, null)
        ).isInstanceOf(IllegalArgumentException.class)
         .satisfies(exc -> LOG.error(exc.getMessage()));

        Dag modified = environment.getBusinessParameters().get(SimpleTVGroup.instance(), query);
        assertEquals(Dag.MAANDAG, modified);
    }


    private String get(Dag dag){
        return get(MappedTVGroup.instance(), MappedTVGroup.programQuery(dag));
    }

    private String get(Slot slot) {
        return get(RangedTVGroup.instance(), RangedTVGroup.programQuery(slot));
    }
    private String get(Dag dag, Slot slot) { return get(MappedRangedTVGroup.instance(), MappedRangedTVGroup.programQuery(dag, slot)); }

    private <ET extends EntryType, T> T get(ParameterGroup<ET> group, Query<ET, T> query) {
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
