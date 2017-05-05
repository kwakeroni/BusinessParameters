package be.kwakeroni.parameters.adapter.jmx;

import javax.management.*;
import javax.management.modelmbean.*;
import java.util.Arrays;
import java.util.Map;

/**
 * Created by kwakeroni on 04/05/17.
 */
class GroupMBean implements DynamicMBean {

    private final String groupName;
    private final MBeanInfo mbeanInfo;

    public GroupMBean(String groupName){
        this.groupName = groupName;
        this.mbeanInfo = createMBeanInfo(groupName);
    }

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
        return this.mbeanInfo;
    }

    public String getGroupName(){
        return this.groupName;
    }

    private static MBeanInfo createMBeanInfo(String name) {

        final Descriptor descriptor = new DescriptorSupport();
        descriptor.setField("name", name);
        descriptor.setField("descriptorType", "mbean");

        return new MBeanInfo(
                        GroupMBean.class.getName(),
                        "Provides access to parameters of group " + name,
                        null,      // attributes
                        null,      // constructors
                        operations(),
                        null,      // notifications
                        descriptor );

    }

    private static MBeanOperationInfo[] operations() {

        final MBeanParameterInfo key =
                new MBeanParameterInfo(
                        "key",
                        String.class.getName(),
                        "The map key" );

        final ImmutableDescriptor parameterInfoDescriptor = new ImmutableDescriptor(
                new String[]{"legalValues"}, new Object[]{ new Object[]{ "key", "one", "two" } }
        );

        final MBeanParameterInfo parameter =
                new MBeanParameterInfo(
                        "parameter",
                        String.class.getName(),
                        "The requested parameter name",
                        parameterInfoDescriptor);

        final MBeanOperationInfo getValue =
                new MBeanOperationInfo(
                        "getValue",
                        "retrieve a parameter value for the selected entry",
                        new MBeanParameterInfo[] {key, parameter},
                        String.class.getName(),
                        ModelMBeanOperationInfo.INFO );

        final MBeanOperationInfo getEntry =
                new MBeanOperationInfo(
                        "getEntry",
                        "retrieve the selected entry",
                        new MBeanParameterInfo[] {key},
                        Map.class.toString(),
                        ModelMBeanOperationInfo.INFO );

        return new MBeanOperationInfo[]{
                getValue, getEntry
        };


    }

}
