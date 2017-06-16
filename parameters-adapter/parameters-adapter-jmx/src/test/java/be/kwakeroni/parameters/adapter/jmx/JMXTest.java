package be.kwakeroni.parameters.adapter.jmx;

import org.junit.Assume;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.management.*;
import javax.management.modelmbean.*;
import javax.swing.*;
import java.lang.management.ManagementFactory;
import java.util.Arrays;
import java.util.Map;

/**
 * Created by kwakeroni on 02/05/17.
 */
public class JMXTest {

    @BeforeClass
    public static void checkRun() {
        Assume.assumeFalse("Skipping interactive test in maven", isMavenRun());
    }

    @Test
    public void test() throws Exception {
        MBeanServer mbeanServer = ManagementFactory.getPlatformMBeanServer();
        Object mbean = dynamicMBean();
        final ObjectName objectName = new ObjectName("be.kwakeroni.parameters:backend=direct");
        mbeanServer.registerMBean(mbean, objectName);

        JOptionPane.showMessageDialog(null, "Click OK to end test.");
    }


    private static boolean isMavenRun() {
        try {
            StackTraceElement[] stackTrace = new Exception().getStackTrace();
            return (stackTrace[stackTrace.length - 1].getClassName().contains("maven"));
        } catch (Exception exc) {
            return false;
        }
    }


    private DynamicMBean dynamicMBean() throws Exception {
        return new DynamicTestBean();
    }

    private static ModelMBean modelMBean() throws Exception {

        final Descriptor descriptor = new DescriptorSupport();
        descriptor.setField("name", "ParametersMappedGroupBean");
        descriptor.setField("descriptorType", "mbean");

        final ModelMBeanInfoSupport modelMBeanInfo =
                new ModelMBeanInfoSupport(
                        TestBean.class.getName(),
                        "A group.",
                        null,      // attributes
                        null,      // constructors
                        operations(),
                        null,      // notifications
                        descriptor);

        ModelMBean modelmbean = new RequiredModelMBean(modelMBeanInfo);
        modelmbean.setManagedResource(new TestBean(), "ObjectReference");
        return modelmbean;
    }

    private static ModelMBeanOperationInfo[] operations() {

        final MBeanParameterInfo key =
                new MBeanParameterInfo(
                        "key",
                        String.class.getName(),
                        "The map key");

        final ImmutableDescriptor parameterInfoDescriptor = new ImmutableDescriptor(
                new String[]{"legalValues"}, new Object[]{new Object[]{"key", "one", "two"}}
        );

        final MBeanParameterInfo parameter =
                new MBeanParameterInfo(
                        "parameter",
                        String.class.getName(),
                        "The requested parameter name",
                        parameterInfoDescriptor);

        final ModelMBeanOperationInfo getValue =
                new ModelMBeanOperationInfo(
                        "getValue",
                        "retrieve a parameter value for the selected entry",
                        new MBeanParameterInfo[]{key, parameter},
                        String.class.getName(),
                        ModelMBeanOperationInfo.INFO);

        final ModelMBeanOperationInfo getEntry =
                new ModelMBeanOperationInfo(
                        "getEntry",
                        "retrieve the selected entry",
                        new MBeanParameterInfo[]{key},
                        Map.class.toString(),
                        ModelMBeanOperationInfo.INFO);

        return new ModelMBeanOperationInfo[]{
                getValue, getEntry
        };


    }


    public static class TestBean {

    }

    public static class DynamicTestBean implements DynamicMBean {
        @Override
        public Object getAttribute(String attribute) throws AttributeNotFoundException, MBeanException, ReflectionException {
            return null;
        }

        @Override
        public void setAttribute(Attribute attribute) throws AttributeNotFoundException, InvalidAttributeValueException, MBeanException, ReflectionException {

        }

        @Override
        public AttributeList getAttributes(String[] attributes) {
            return null;
        }

        @Override
        public AttributeList setAttributes(AttributeList attributes) {
            return null;
        }

        @Override
        public Object invoke(String actionName, Object[] params, String[] signature) throws MBeanException, ReflectionException {
            String msg = String.format("Invoke %s(%s, %s)", actionName, Arrays.deepToString(params), Arrays.toString(signature));
            System.out.println(msg);
            return msg;
        }

        @Override
        public MBeanInfo getMBeanInfo() {
            final Descriptor descriptor = new DescriptorSupport();
            descriptor.setField("name", "ParametersMappedGroupBean");
            descriptor.setField("descriptorType", "mbean");

            return new MBeanInfo(
                    TestBean.class.getName(),
                    "A group.",
                    null,      // attributes
                    null,      // constructors
                    operations(),
                    null,      // notifications
                    descriptor);
        }
    }
}
