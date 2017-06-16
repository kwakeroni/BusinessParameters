package be.kwakeroni.parameters.adapter.jmx.api;

import java.util.List;

/**
 * Created by kwakeroni on 15/05/17.
 */
class DefaultJMXOperation implements JMXOperation {

    private final String name;
    private final List<String> operationTypes;

    public DefaultJMXOperation(String name, List<String> operationTypes) {
        this.name = name;
        this.operationTypes = operationTypes;
    }

    @Override
    public JMXInvocation withParameters(Object[] parameters, String[] signature) {
        return new DefaultJMXInvocation(name, operationTypes, parameters, signature);
    }
}
