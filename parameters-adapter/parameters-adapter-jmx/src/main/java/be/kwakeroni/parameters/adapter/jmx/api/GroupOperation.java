package be.kwakeroni.parameters.adapter.jmx.api;

/**
 * Created by kwakeroni on 11/05/17.
 */
public interface GroupOperation {

    public JMXOperationAction withParameters(Object[] parameters, String[] signature);

}
