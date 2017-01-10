package be.kwakeroni.scratch;

import be.kwakeroni.parameters.basic.client.model.Entry;
import be.kwakeroni.parameters.basic.client.model.Range;
import be.kwakeroni.parameters.basic.client.query.EntryQuery;
import be.kwakeroni.parameters.basic.client.query.MappedQuery;
import be.kwakeroni.parameters.basic.client.query.RangedQuery;
import be.kwakeroni.parameters.basic.client.query.ValueQuery;
import be.kwakeroni.scratch.tv.*;
import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * (C) 2016 Maarten Van Puymbroeck
 */
public class MinimalTest {

    Environment environment = new Environment();

    @Test
    public void testSimpleValueQuery(){
        Dag dag = environment.getBusinessParameters().get(SimpleTVGroup.instance(),
                new ValueQuery<>(SimpleTVGroup.DAY));
        assertEquals(Dag.MAANDAG, dag);
    }

    @Test
    public void testSimpleEntryQuery(){
        Entry entry = environment.getBusinessParameters().get(SimpleTVGroup.instance(), new EntryQuery());
        assertEquals(Dag.MAANDAG, entry.getValue(SimpleTVGroup.DAY));
        assertEquals(Slot.atHour(20), entry.getValue(SimpleTVGroup.SLOT));
    }

    @Test
    public void testMappedValueQuery(){
        String program = environment.getBusinessParameters().get(MappedTVGroup.instance(),
                new MappedQuery<>(Dag.ZATERDAG, Dag::toString,
                        new ValueQuery<>(MappedTVGroup.PROGRAM)));
        assertEquals("Samson", program);
    }

    @Test
    public void testMappedEntryQuery(){
        Entry entry = environment.getBusinessParameters().get(MappedTVGroup.instance(),
                new MappedQuery<>(Dag.ZONDAG, Dag::toString,
                        new EntryQuery()));
        assertEquals(Dag.ZONDAG, entry.getValue(MappedTVGroup.DAY));
        assertEquals("Morgen Maandag", entry.getValue(MappedTVGroup.PROGRAM));
    }

    @Test
    public void testRangedValueQuery(){
        String program = environment.getBusinessParameters().get(RangedTVGroup.instance(),
                new RangedQuery<>(Slot.atHalfPast(9), Slot::toString,
                        new ValueQuery<>(RangedTVGroup.PROGRAM)));
        assertEquals("Samson", program);
    }

    @Test
    public void testRangedEntryQuery(){
        Entry entry = environment.getBusinessParameters().get(RangedTVGroup.instance(),
                new RangedQuery<>(Slot.atHour(21), Slot::toString,
                        new EntryQuery()));
        assertEquals(Range.of(Slot.atHalfPast(20), Slot.atHour(22)), entry.getValue(RangedTVGroup.SLOT));
        assertEquals("Morgen Maandag", entry.getValue(RangedTVGroup.PROGRAM));
    }

    @Test
    public void testMappedRangedValueQuery(){
        assertEquals("Samson", getMappedRangedProgram(Dag.ZATERDAG, Slot.atHalfPast(11)));
        assertEquals("Koers", getMappedRangedProgram(Dag.ZATERDAG, Slot.atHour(14)));
        assertEquals("Morgen Maandag", getMappedRangedProgram(Dag.ZONDAG, Slot.atHour(21)));
        assertEquals("Gisteren Zondag", getMappedRangedProgram(Dag.MAANDAG, Slot.atHour(21)));
    }

    private String getMappedRangedProgram(Dag day, Slot slot){
        return environment.getBusinessParameters().get(MappedRangedTVGroup.instance(),
                new MappedQuery<>(day, Dag::toString,
                    new RangedQuery<>(slot, Slot::toString,
                        new ValueQuery<>(MappedRangedTVGroup.PROGRAM))));
    }
}
