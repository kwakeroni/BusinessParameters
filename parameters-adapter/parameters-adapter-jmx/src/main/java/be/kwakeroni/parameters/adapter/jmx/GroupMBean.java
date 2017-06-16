package be.kwakeroni.parameters.adapter.jmx;

import be.kwakeroni.parameters.adapter.jmx.api.JMXOperation;
import be.kwakeroni.parameters.adapter.jmx.api.JMXInvocation;
import be.kwakeroni.parameters.backend.api.BusinessParametersBackend;
import be.kwakeroni.parameters.backend.api.query.BackendQuery;
import be.kwakeroni.parameters.backend.api.query.BackendWireFormatterContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.management.*;
import java.util.Map;
import java.util.Objects;

/**
 * Created by kwakeroni on 04/05/17.
 */
class GroupMBean implements DynamicMBean {

    private static final Logger LOG = LoggerFactory.getLogger(GroupMBean.class);

    private final String groupName;
    private final MBeanInfo mbeanInfo;
    private final Map<String, JMXOperation> operations;
    private final BusinessParametersBackend<?> backend;
    private final BackendWireFormatterContext wireFormatterContext;

    GroupMBean(String groupName, MBeanInfo mbeanInfo, Map<String, JMXOperation> operations, BusinessParametersBackend<?> backend, BackendWireFormatterContext wireFormatterContext) {
        this.groupName = Objects.requireNonNull(groupName, "groupName");
        this.mbeanInfo = Objects.requireNonNull(mbeanInfo, "mbeanInfo");
        this.operations = operations;
        this.backend = backend;
        this.wireFormatterContext = wireFormatterContext;
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
    public MBeanInfo getMBeanInfo() {
        return this.mbeanInfo;
    }

    public String getGroupName() {
        return this.groupName;
    }

    @Override
    public Object invoke(String actionName, Object[] params, String[] signature) throws MBeanException, ReflectionException {
        JMXInvocation invocation = this.operations.get(actionName).withParameters(params, signature);
        return execute(this.groupName, invocation);
    }

    private Object execute(String groupName, JMXInvocation invocation) {
        try {
            return execute(groupName, invocation, this.backend);
        } catch (RuntimeException exc) {
            LOG.error("Could not execute invocation {} on group {}", invocation.getOperationType(), groupName, exc);
            throw exc;
        }
    }

    private <Q> Object execute(String groupName, JMXInvocation invocation, BusinessParametersBackend<Q> backend) {
        BackendQuery<? extends Q, ?> query = backend.internalizeQuery(groupName, invocation, this.wireFormatterContext);
        return execute(groupName, query, backend);
    }

    private <Q, V> Object execute(String groupName, BackendQuery<? extends Q, V> query, BusinessParametersBackend<Q> backend) {
        V result = backend.select(groupName, query);
        return query.externalizeValue(result, this.wireFormatterContext);
    }

}
