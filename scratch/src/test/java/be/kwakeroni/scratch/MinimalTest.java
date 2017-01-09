package be.kwakeroni.scratch;

import be.kwakeroni.parameters.basic.client.query.ValueQuery;
import be.kwakeroni.scratch.tv.Dag;
import be.kwakeroni.scratch.tv.SimpleTVGroup;
import org.junit.Assert;
import org.junit.Test;

/**
 * (C) 2016 Maarten Van Puymbroeck
 */
public class MinimalTest {

    Environment environment = new Environment();

    @Test
    public void testSimpleValue(){
        Dag dag = environment.getBusinessParameters().get(SimpleTVGroup.instance(), new ValueQuery<>(SimpleTVGroup.DAY));
        Assert.assertNotNull(dag);
    }

    
}
