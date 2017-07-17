package be.kwakeroni.parameters.basic.definition.jmx.factory;

import be.kwakeroni.parameters.backend.api.factory.BackendWireFormatterFactory;
import be.kwakeroni.parameters.basic.backend.query.BasicBackendWireFormatter;
import be.kwakeroni.parameters.basic.definition.jmx.BasicJMXBackendWireFormatter;

/**
 * Created by kwakeroni on 15/05/17.
 */
public class BasicJMXBackendWireFormatterFactory implements BackendWireFormatterFactory {

    @Override
    public String getWireFormat() {
        return "jmx";
    }

    @Override
    public void visitInstances(Visitor visitor) {
        visitor.visit(BasicBackendWireFormatter.class, new BasicJMXBackendWireFormatter());
    }
}
