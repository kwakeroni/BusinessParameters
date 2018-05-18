package be.kwakeroni.scratch;

import be.kwakeroni.parameters.adapter.jmx.JMXBackendAdapter;
import be.kwakeroni.parameters.adapter.jmx.factory.JMXBackendAdapterFactory;
import be.kwakeroni.scratch.env.inmemory.TransientInMemoryTestData;
import org.junit.Assume;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.swing.*;

/**
 * Created by kwakeroni on 04/05/17.
 */
public class JMXTest {

    private JMXBackendAdapter jmx;

    @BeforeClass
    public static void checkRun() {
        Assume.assumeFalse("Skipping interactive test in maven", isMavenRun());
    }

    @Before
    public void setUp() {
        TransientInMemoryTestData testData = new TransientInMemoryTestData();
        JMXBackendAdapterFactory factory = new JMXBackendAdapterFactory();
        factory.setBackendType(testData::acceptBackend);
        jmx = factory.newInstance();
    }

    @Test
    public void test() {
        JOptionPane.showMessageDialog(null, "Click OK to end test.");
    }

    private static boolean isMavenRun() {
        try {
//            new Exception().printStackTrace();
//            return Class.forName("org.apache.maven.surefire.booter.Classpath") != null;
            StackTraceElement[] stackTrace = new Exception().getStackTrace();
            return (stackTrace[stackTrace.length - 1].getClassName().contains("maven"));
        } catch (Exception exc) {
            return false;
        }
    }

}
