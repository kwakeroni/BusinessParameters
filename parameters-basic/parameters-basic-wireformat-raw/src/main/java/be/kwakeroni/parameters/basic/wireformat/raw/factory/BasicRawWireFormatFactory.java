package be.kwakeroni.parameters.basic.wireformat.raw.factory;

import be.kwakeroni.parameters.backend.api.factory.BackendWireFormatterFactory;
import be.kwakeroni.parameters.basic.backend.query.BasicBackendWireFormatter;
import be.kwakeroni.parameters.basic.client.query.BasicClientWireFormatter;
import be.kwakeroni.parameters.basic.wireformat.raw.BasicRawWireFormat;
import be.kwakeroni.parameters.client.api.factory.ClientWireFormatterFactory;

/**
 * (C) 2016 Maarten Van Puymbroeck
 */
public class BasicRawWireFormatFactory implements ClientWireFormatterFactory, BackendWireFormatterFactory {

    private static final BasicRawWireFormat WIRE_FORMAT = new BasicRawWireFormat();

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
