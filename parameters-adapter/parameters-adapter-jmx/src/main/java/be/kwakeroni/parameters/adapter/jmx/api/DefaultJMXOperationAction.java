package be.kwakeroni.parameters.adapter.jmx.api;

import java.util.List;

/**
 * Created by kwakeroni on 11/05/17.
 */
class DefaultJMXOperationAction implements JMXOperationAction {

    private final String name;
    private final Object[] parameters;
    private final String[] signature;
    private final List<String> actionTypes;
    private int parameterPointer;

    public DefaultJMXOperationAction(String name, List<String> actionTypes, Object[] parameters, String[] signature) {
        this(name, actionTypes, parameters, signature, 0);
    }

    private DefaultJMXOperationAction(String name, List<String> actionTypes, Object[] parameters, String[] signature, int parameterPointer) {
        this.name = name;
        this.actionTypes = actionTypes;
        this.parameters = parameters;
        this.signature = signature;
        this.parameterPointer = parameterPointer;
    }

    @Override
    public String getActionType() {
        return actionTypes.get(0);
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
    public JMXOperationAction pop() {
        return new DefaultJMXOperationAction(name, actionTypes.subList(1, actionTypes.size()), parameters, signature, parameterPointer);
    }
}
