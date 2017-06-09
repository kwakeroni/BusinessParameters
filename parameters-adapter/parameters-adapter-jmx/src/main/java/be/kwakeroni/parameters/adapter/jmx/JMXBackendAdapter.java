package be.kwakeroni.parameters.adapter.jmx;

import be.kwakeroni.parameters.adapter.jmx.api.JMXGroupBuilder;
import be.kwakeroni.parameters.backend.api.BusinessParametersBackend;
import be.kwakeroni.parameters.backend.api.query.BackendWireFormatterContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.management.JMException;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import java.lang.management.ManagementFactory;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by kwakeroni on 04/05/17.
 */
public class JMXBackendAdapter {

    Logger LOG = LoggerFactory.getLogger(JMXBackendAdapter.class);

    private final MBeanServer mbeanServer;
    private final JMXGroupMBeanFactoryContext factoryContext;
    private final BackendWireFormatterContext wireFormatterContext;
    private final Map<BusinessParametersBackend<?>, Map<String, Object>> beans = new HashMap<>();

    public JMXBackendAdapter(JMXGroupMBeanFactoryContext factoryContext, BackendWireFormatterContext wireFormatterContext) {
        this(ManagementFactory.getPlatformMBeanServer(), factoryContext, wireFormatterContext);
    }

    public JMXBackendAdapter(MBeanServer server, JMXGroupMBeanFactoryContext factoryContext, BackendWireFormatterContext wireFormatterContext) {
        this.mbeanServer = server;
        this.factoryContext = factoryContext;
        this.wireFormatterContext = wireFormatterContext;
    }


    public void register(BusinessParametersBackend<?> backend) {
        backend.getGroupNames().forEach(name -> register(backend, name));
    }

    public void unregister(BusinessParametersBackend<?> backend) {
        backend.getGroupNames().forEach(name -> unregister(backend, name));
        this.beans.remove(backend);
    }

    private void register(BusinessParametersBackend<?> backend, String groupName) {
        register(backend, createMBeanBuilder(backend, groupName));
    }

    private void register(BusinessParametersBackend<?> backend, JMXGroupBuilder builder) {
        String groupName = builder.getGroupName();
        GroupMBean mbean = new GroupMBean(groupName, builder.getMBeanInfo(GroupMBean.class), builder.getOperationsByName(), backend, wireFormatterContext);
        try {
            mbeanServer.registerMBean(mbean, getObjectName(backend, groupName));
        } catch (JMException exc) {
            LOG.error("Unable to register JMX bean for group " + groupName, exc);
        }
        this.beans.computeIfAbsent(backend, be -> new HashMap<>(be.getGroupNames().size())).put(groupName, mbean);
    }

    private void unregister(BusinessParametersBackend<?> backend, String groupName){
        try {
            mbeanServer.unregisterMBean(getObjectName(backend, groupName));
        } catch (JMException exc){
            LOG.error("Unable to unregister JMX bean for group " + groupName, exc);
        }
    }

    private void unregister(String name) throws JMException {
        final ObjectName objectName = new ObjectName(name);
        mbeanServer.unregisterMBean(objectName);
    }

    private JMXGroupBuilder createMBeanBuilder(BusinessParametersBackend<?> backend, String name) {
        return backend.getDefinition(name).apply(this.factoryContext);
    }

    private ObjectName getObjectName(BusinessParametersBackend<?> backend, String groupName) throws JMException {
        String name = "be.kwakeroni.parameters:backend=" + backend.toString() + ",group=" + groupName;
        return new ObjectName(name);
    }
}
