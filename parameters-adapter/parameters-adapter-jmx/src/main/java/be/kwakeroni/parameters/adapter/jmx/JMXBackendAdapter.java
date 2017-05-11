package be.kwakeroni.parameters.adapter.jmx;

import be.kwakeroni.parameters.adapter.jmx.api.JMXGroupBuilder;
import be.kwakeroni.parameters.backend.api.BusinessParametersBackend;
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
    private final Map<BusinessParametersBackend<?>, Map<String, Object>> beans = new HashMap<>();

    public JMXBackendAdapter(JMXGroupMBeanFactoryContext factoryContext) {
        this(ManagementFactory.getPlatformMBeanServer(), factoryContext);
    }

    public JMXBackendAdapter(MBeanServer server, JMXGroupMBeanFactoryContext factoryContext) {
        this.mbeanServer = server;
        this.factoryContext = factoryContext;
    }


    public void register(BusinessParametersBackend<?> backend) {
        backend.getGroupNames().forEach(name -> register(backend, name));
    }

    private void register(BusinessParametersBackend<?> backend, String groupName) {
        register(backend, createMBeanBuilder(backend, groupName));
    }

    private void register(BusinessParametersBackend<?> backend, JMXGroupBuilder builder) {
        String groupName = builder.getGroupName();
        Object mbean = builder.build();
        try {
            register(backend, "be.kwakeroni.parameters:backend=" + backend.toString() + ",group=" + groupName, mbean);
        } catch (JMException exc) {
            LOG.error("Unable to register JMX bean for group " + groupName, exc);
        }
        this.beans.computeIfAbsent(backend, be -> new HashMap<>(be.getGroupNames().size())).put(groupName, mbean);
    }

    private void register(BusinessParametersBackend<?> backend, String name, Object mbean) throws JMException {
        final ObjectName objectName = new ObjectName(name);
        mbeanServer.registerMBean(mbean, objectName);

    }

    private JMXGroupBuilder createMBeanBuilder(BusinessParametersBackend<?> backend, String name) {
        return backend.getDefinition(name).apply(this.factoryContext);
    }

}
