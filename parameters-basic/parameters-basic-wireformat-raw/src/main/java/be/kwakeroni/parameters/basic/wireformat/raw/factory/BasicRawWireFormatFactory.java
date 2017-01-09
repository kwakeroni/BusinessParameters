package be.kwakeroni.parameters.basic.wireformat.raw.factory;

import be.kwakeroni.parameters.api.backend.factory.InternalizerFactory;
import be.kwakeroni.parameters.api.client.factory.ExternalizerFactory;
import be.kwakeroni.parameters.api.client.query.Externalizer;
import be.kwakeroni.parameters.basic.client.query.BasicExternalizer;
import be.kwakeroni.parameters.basic.wireformat.raw.BasicRawWireFormat;

import java.util.function.BiConsumer;

/**
 * (C) 2016 Maarten Van Puymbroeck
 */
public class BasicRawWireFormatFactory implements ExternalizerFactory, InternalizerFactory {

    private static final BasicRawWireFormat WIRE_FORMAT = new BasicRawWireFormat();

    @Override
    public void registerInstance(ExternalizerFactory.Registry registry) {
        registry.register(BasicExternalizer.class, WIRE_FORMAT);
    }

    @Override
    public void registerInstance(InternalizerFactory.Registry registry) {
        registry.register(WIRE_FORMAT);
    }
}
