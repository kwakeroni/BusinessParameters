package be.kwakeroni.parameters.basic.wireformat.json.factory;

import be.kwakeroni.parameters.backend.api.factory.BackendWireFormatterFactory;
import be.kwakeroni.parameters.basic.backend.query.BasicBackendWireFormatter;
import be.kwakeroni.parameters.basic.client.query.BasicClientWireFormatter;
import be.kwakeroni.parameters.basic.wireformat.json.BasicJsonWireFormat;
import be.kwakeroni.parameters.client.api.factory.ClientWireFormatterFactory;

/**
 * Created by kwakeroni on 10/10/17.
 */
public class BasicJsonWireFormatFactory implements ClientWireFormatterFactory, BackendWireFormatterFactory {

    private static final BasicJsonWireFormat WIRE_FORMAT = new BasicJsonWireFormat();

    @Override
    public String getWireFormat() {
        return WIRE_FORMAT.getType();
    }

    @Override
    public void visitInstances(ClientWireFormatterFactory.Visitor visitor) {
        visitor.visit(BasicClientWireFormatter.class, WIRE_FORMAT);
    }

    @Override
    public void visitInstances(BackendWireFormatterFactory.Visitor visitor) {
        visitor.visit(BasicBackendWireFormatter.class, WIRE_FORMAT);
    }
}

