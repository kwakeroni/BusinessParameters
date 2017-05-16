package be.kwakeroni.parameters.adapter.jmx.api;

import java.util.List;

/**
 * Created by kwakeroni on 15/05/17.
 */
class DefaultGroupOperation implements GroupOperation {

    private final String name;
    private final List<String> actionTypes;

    public DefaultGroupOperation(String name, List<String> actionTypes) {
        this.name = name;
        this.actionTypes = actionTypes;
    }

    @Override
    public JMXOperationAction withParameters(Object[] parameters, String[] signature) {
        return new DefaultJMXOperationAction(name, actionTypes, parameters, signature);
    }
}
