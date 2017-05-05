package be.kwakeroni.scratch;

import be.kwakeroni.parameters.adapter.jmx.JMXBackendAdapter;
import be.kwakeroni.parameters.adapter.jmx.factory.JMXBackendAdapterFactory;
import org.junit.Before;
import org.junit.Test;

import javax.swing.*;

/**
 * Created by kwakeroni on 04/05/17.
 */
public class JMXTest {

    private JMXBackendAdapter jmx;

    @Before
    public void setUp(){
        InMemoryTestData testData = new InMemoryTestData();
        JMXBackendAdapterFactory factory = new JMXBackendAdapterFactory();
        factory.setBackendType(testData::acceptBackend);
        jmx = factory.newInstance();
    }

    @Test
    public void test(){
        JOptionPane.showMessageDialog(null, "Click OK to end test.");
    }

}
