package be.kwakeroni.scratch;

import be.kwakeroni.parameters.basic.client.model.Entry;
import be.kwakeroni.parameters.basic.client.query.EntryQuery;
import be.kwakeroni.parameters.basic.client.query.ValueQuery;
import be.kwakeroni.scratch.tv.Dag;
import be.kwakeroni.scratch.tv.SimpleTVGroup;
import be.kwakeroni.scratch.tv.Slot;
import org.junit.Assert;
import org.junit.Test;

/**
 * (C) 2016 Maarten Van Puymbroeck
 */
public class MinimalTest {

    Environment environment = new Environment();

    @Test
    public void testSimpleValueQuery(){
        Dag dag = environment.getBusinessParameters().get(SimpleTVGroup.instance(), new ValueQuery<>(SimpleTVGroup.DAY));
        Assert.assertEquals(Dag.MAANDAG, dag);
    }

    @Test
    public void testSimpleEntryQuery(){
        Entry entry = environment.getBusinessParameters().get(SimpleTVGroup.instance(), new EntryQuery());
        Assert.assertEquals(Dag.MAANDAG, entry.getValue(SimpleTVGroup.DAY));
        Assert.assertEquals(Slot.atHour(20), entry.getValue(SimpleTVGroup.SLOT));
    }


}
