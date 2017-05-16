package be.kwakeroni.parameters.adapter.jmx.api;

import javax.management.Descriptor;
import javax.management.ImmutableDescriptor;
import javax.management.MBeanOperationInfo;
import javax.management.MBeanParameterInfo;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by kwakeroni on 10/05/17.
 */
class DefaultJMXOperationBuilder implements JMXOperationBuilder {

    private String name;
    private String description;
    private int infoType;
    private List<MBeanParameterInfo> parameters = new ArrayList<>();
    private List<String> operationTypes = new ArrayList<>();


    DefaultJMXOperationBuilder(int infoType) {
        this.infoType = infoType;
    }

    @Override
    public JMXOperationBuilder withName(String name) {
        this.name = name;
        return this;
    }

    @Override
    public JMXOperationBuilder withDescription(String description) {
        this.description = description;
        return this;
    }

    @Override
    public JMXOperationBuilder pushType(String type) {
        this.operationTypes.add(0, type);
        return this;
    }

    @Override
    public JMXOperationBuilder prependParameter(String name) {
        return prependParameter(name, null);
    }

    @Override
    public JMXOperationBuilder prependParameter(String name, Collection<?> legalValues) {
        this.parameters.add(0, parameter(name, legalValues));
        return this;
    }

    @Override
    public JMXOperationBuilder appendParameter(String name) {
        return appendParameter(name, null);
    }

    @Override
    public JMXOperationBuilder appendParameter(String name, Collection<?> legalValues) {
        this.parameters.add(parameter(name, legalValues));
        return this;
    }

    private MBeanParameterInfo parameter(String name, Collection<?> legalValues) {
        return new MBeanParameterInfo(name, String.class.getName(), "The " + name + " parameter", parameterDescriptor(legalValues));
    }

    private Descriptor parameterDescriptor(Collection<?> legalValues) {
        if (legalValues == null) {
            return null;
        }
        return new ImmutableDescriptor(
                new String[]{"legalValues"}, new Object[]{legalValues.toArray()}
        );
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public MBeanOperationInfo toOperationInfo() {
        this.parameters.toArray(new MBeanParameterInfo[0]);
        return new MBeanOperationInfo(
                this.name,
                this.description,
                this.parameters.toArray(new MBeanParameterInfo[0]),
                String.class.getName(),
                this.infoType);
    }

    @Override
    public GroupOperation toGroupOperation() {
        return new DefaultGroupOperation(this.name, this.operationTypes);
    }
}
