package be.kwakeroni.scratch;

import be.kwakeroni.parameters.basic.client.model.Entry;
import be.kwakeroni.parameters.basic.client.model.Mapped;
import be.kwakeroni.parameters.basic.client.model.Range;
import be.kwakeroni.parameters.basic.client.model.Ranged;
import be.kwakeroni.parameters.basic.client.model.Simple;
import be.kwakeroni.parameters.basic.client.query.EntryQuery;
import be.kwakeroni.parameters.basic.client.query.MappedQuery;
import be.kwakeroni.parameters.basic.client.query.RangedQuery;
import be.kwakeroni.parameters.basic.client.query.ValueQuery;
import be.kwakeroni.parameters.client.api.query.Query;
import be.kwakeroni.scratch.tv.Dag;
import be.kwakeroni.scratch.tv.MappedTVGroup;
import be.kwakeroni.scratch.tv.RangedTVGroup;
import be.kwakeroni.scratch.tv.SimpleTVGroup;
import be.kwakeroni.scratch.tv.Slot;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * (C) 2017 Maarten Van Puymbroeck
 */
public class MinimalWriteTest {


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

        Entry entry = environment.getBusinessParameters().get(RangedTVGroup.instance(), query);
        assertEquals(Range.of(Slot.atHour(8), Slot.atHour(12)), entry.getValue(RangedTVGroup.SLOT));
        assertEquals("Samson", entry.getValue(RangedTVGroup.PROGRAM));

        environment.getWritableBusinessParameters().set(RangedTVGroup.instance(), query, RangedTVGroup.entry(Slot.atHour(10), Slot.atHour(12), "TikTak"));

        Entry old = environment.getBusinessParameters().get(RangedTVGroup.instance(), query);
        assertNull(old);

        Entry modified = environment.getBusinessParameters().get(RangedTVGroup.instance(),
                new RangedQuery<>(Slot.atHour(10), Slot.type, new EntryQuery()));
        assertEquals(Range.of(Slot.atHour(10), Slot.atHour(12)), modified.getValue(RangedTVGroup.SLOT));
        assertEquals("TikTak", modified.getValue(MappedTVGroup.PROGRAM));
    }

}
