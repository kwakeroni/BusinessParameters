package be.kwakeroni.parameters.basic.wireformat.raw.factory;

import be.kwakeroni.parameters.backend.api.factory.BackendWireFormatterFactory;
import be.kwakeroni.parameters.client.api.factory.ClientWireFormatterFactory;
import be.kwakeroni.parameters.basic.backend.query.BasicBackendWireFormatter;
import be.kwakeroni.parameters.basic.client.query.BasicClientWireFormatter;
import be.kwakeroni.parameters.basic.wireformat.raw.BasicRawWireFormat;

/**
 * (C) 2016 Maarten Van Puymbroeck
 */
public class BasicRawWireFormatFactory implements ClientWireFormatterFactory, BackendWireFormatterFactory {

    private static final BasicRawWireFormat WIRE_FORMAT = new BasicRawWireFormat();

    @Override
    public void registerInstance(ClientWireFormatterFactory.Registry registry) {
        registry.register(BasicClientWireFormatter.class, WIRE_FORMAT);
    }

    @Override
    public void registerInstance(BackendWireFormatterFactory.Registry registry) {
        registry.register(BasicBackendWireFormatter.class, WIRE_FORMAT);
    }
}
