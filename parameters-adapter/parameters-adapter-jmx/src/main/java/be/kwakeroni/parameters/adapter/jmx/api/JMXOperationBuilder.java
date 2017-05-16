package be.kwakeroni.parameters.adapter.jmx.api;

import javax.management.MBeanOperationInfo;
import javax.management.modelmbean.ModelMBeanOperationInfo;
import java.util.Collection;

/**
 * Created by kwakeroni on 10/05/17.
 */
public interface JMXOperationBuilder {

    public static JMXOperationBuilder newQuery() {
        return new DefaultJMXOperationBuilder(ModelMBeanOperationInfo.INFO);
    }

    public JMXOperationBuilder withName(String name);

    public JMXOperationBuilder withDescription(String description);

    public JMXOperationBuilder pushType(String type);

    public JMXOperationBuilder prependParameter(String name);

    public JMXOperationBuilder prependParameter(String name, Collection<?> legalValues);

    public JMXOperationBuilder appendParameter(String name);

    public JMXOperationBuilder appendParameter(String name, Collection<?> legalValues);

    public String getName();

    public MBeanOperationInfo toOperationInfo();

    public JMXOperation toOperation();
}
