package be.kwakeroni.parameters.adapter.jmx.api;

import javax.management.*;
import java.util.Arrays;
import java.util.Objects;

/**
 * Created by kwakeroni on 04/05/17.
 */
class GroupMBean implements DynamicMBean {

    private final String groupName;
    private final MBeanInfo mbeanInfo;

    GroupMBean(String groupName, MBeanInfo mbeanInfo) {
        this.groupName = Objects.requireNonNull(groupName, "groupName");
        this.mbeanInfo = Objects.requireNonNull(mbeanInfo, "mbeanInfo");
    }

    @Override
    public Object getAttribute(String attribute) throws AttributeNotFoundException, MBeanException, ReflectionException {
        throw new AttributeNotFoundException("Business Parameter Groups do not expose any attributes");
    }

    @Override
    public void setAttribute(Attribute attribute) throws AttributeNotFoundException, InvalidAttributeValueException, MBeanException, ReflectionException {
        throw new AttributeNotFoundException("Business Parameter Groups do not expose any attributes");
    }

    @Override
    public AttributeList getAttributes(String[] attributes) {
        return new AttributeList();
    }

    @Override
    public AttributeList setAttributes(AttributeList attributes) {
        return new AttributeList();
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

    public String getGroupName() {
        return this.groupName;
    }

}
