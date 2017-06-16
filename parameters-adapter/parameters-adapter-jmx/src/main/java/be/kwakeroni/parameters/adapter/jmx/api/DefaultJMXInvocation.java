package be.kwakeroni.parameters.adapter.jmx.api;

import java.util.List;

/**
 * Created by kwakeroni on 11/05/17.
 */
class DefaultJMXInvocation implements JMXInvocation {

    private final String name;
    private final Object[] parameters;
    private final String[] signature;
    private final List<String> operationTypes;
    private int parameterPointer;

    public DefaultJMXInvocation(String name, List<String> operationTypes, Object[] parameters, String[] signature) {
        this(name, operationTypes, parameters, signature, 0);
    }

    private DefaultJMXInvocation(String name, List<String> operationTypes, Object[] parameters, String[] signature, int parameterPointer) {
        this.name = name;
        this.operationTypes = operationTypes;
        this.parameters = parameters;
        this.signature = signature;
        this.parameterPointer = parameterPointer;
    }

    @Override
    public String getOperationType() {
        return operationTypes.get(0);
    }

    @Override
    public String getParameter(int index) {
        return (String) parameters[index];
    }

    @Override
    public String popParameter() {
        return (String) parameters[parameterPointer++];
    }

    @Override
    public JMXInvocation pop() {
        return new DefaultJMXInvocation(name, operationTypes.subList(1, operationTypes.size()), parameters, signature, parameterPointer);
    }
}
