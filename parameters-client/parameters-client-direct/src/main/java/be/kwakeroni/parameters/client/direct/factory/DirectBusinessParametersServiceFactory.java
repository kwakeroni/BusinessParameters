package be.kwakeroni.parameters.client.direct.factory;

import be.kwakeroni.parameters.backend.api.BusinessParametersBackend;
import be.kwakeroni.parameters.backend.api.factory.BusinessParametersBackendFactory;
import be.kwakeroni.parameters.client.api.BusinessParameters;
import be.kwakeroni.parameters.client.api.WritableBusinessParameters;
import be.kwakeroni.parameters.client.api.factory.BusinessParametersFactory;
import be.kwakeroni.parameters.client.api.factory.ClientWireFormatterFactory;
import be.kwakeroni.parameters.client.direct.BackendRegistry;
import be.kwakeroni.parameters.client.direct.DirectBusinessParametersConnector;
import be.kwakeroni.parameters.client.direct.WireFormatterRegistry;

import java.util.ServiceLoader;

/**
 * (C) 2016 Maarten Van Puymbroeck
 */
public class DirectBusinessParametersServiceFactory implements BusinessParametersFactory {

    @Override
    public BusinessParameters getInstance() {
        return getWritableInstance();
    }

    @Override
    public WritableBusinessParameters getWritableInstance() {
        WireFormatterRegistry registry = new WireFormatterRegistry();
        registerFormatters(registry);
        BackendRegistry backends = new BackendRegistry();
        registerBackends(backends);
        return new DirectBusinessParametersConnector(registry, backends);
    }

    private void registerFormatters(WireFormatterRegistry registry) {
        ServiceLoader<ClientWireFormatterFactory> loader = ServiceLoader.load(ClientWireFormatterFactory.class);
        for (ClientWireFormatterFactory factory : loader) {
            factory.registerInstance(registry::register);
        }
    }

    private void registerBackends(BackendRegistry registry) {
        ServiceLoader<BusinessParametersBackendFactory> loader = ServiceLoader.load(BusinessParametersBackendFactory.class);
        for (BusinessParametersBackendFactory factory : loader) {
            BusinessParametersBackend backend = factory.getInstance();
            registry.register(backend);
        }
    }
}
