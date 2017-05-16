package be.kwakeroni.parameters.adapter.jmx.api;

import javax.management.MBeanInfo;
import java.util.Collection;
import java.util.Map;

/**
 * Created by kwakeroni on 09/05/17.
 */
public interface JMXGroupBuilder {

    public static JMXGroupBuilder withName(String groupName) {
        return new DefaultJMXGroupBuilder(groupName);
    }

    public JMXGroupBuilder withParameters(Collection<String> parameters);

    public JMXOperationBuilder addQuery(String name);

    public JMXOperationBuilder getQuery(String name);

    public String getGroupName();

    public MBeanInfo getMBeanInfo(Class<?> targetClass);

    public Map<String, GroupOperation> getOperationsByName();
}
