package be.kwakeroni.parameters.adapter.jmx.api;

/**
 * Created by kwakeroni on 11/05/17.
 */
public interface JMXOperationAction {


    public String getActionType();

    public String getParameter(int index);

    public String popParameter();

    public JMXOperationAction pop();

}
