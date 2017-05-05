package be.kwakeroni.parameters.adapter.jmx;

import be.kwakeroni.parameters.backend.api.BusinessParametersBackend;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.management.*;
import java.lang.management.ManagementFactory;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by kwakeroni on 04/05/17.
 */
public class JMXBackendAdapter {

    Logger LOG = LoggerFactory.getLogger(JMXBackendAdapter.class);

    private final MBeanServer mbeanServer;
    private final Map<BusinessParametersBackend<?>, Map<String, GroupMBean>> beans = new HashMap<>();

    public JMXBackendAdapter(){
        this(ManagementFactory.getPlatformMBeanServer());
    }

    public JMXBackendAdapter(MBeanServer server){
        this.mbeanServer = server;
    }


    public void register(BusinessParametersBackend<?> backend){
        backend.getGroupNames().forEach(name -> register(backend, name));
    }

    private void register(BusinessParametersBackend<?> backend, String groupName){
        GroupMBean mbean = createMBean(backend, groupName);
        register(backend, mbean);
        this.beans.computeIfAbsent(backend, be -> new HashMap<>(be.getGroupNames().size())).put(groupName, mbean);
    }

    private void register(BusinessParametersBackend<?> backend, GroupMBean mbean){
        try {
            register(backend, "be.kwakeroni.parameters:backend=" + backend.toString() + ",group="+mbean.getGroupName(), mbean);
        } catch (JMException exc) {
            LOG.error("Unable to register JMX bean for group " + mbean.getGroupName(), exc);
        }
    }

    private void register(BusinessParametersBackend<?> backend, String name, GroupMBean mbean) throws JMException {
        final ObjectName objectName = new ObjectName(name);
        mbeanServer.registerMBean(mbean, objectName);

    }

    private GroupMBean createMBean(BusinessParametersBackend<?> backend, String name){
        backend.getDefinition(name);
        return new GroupMBean(name);
    }

}
