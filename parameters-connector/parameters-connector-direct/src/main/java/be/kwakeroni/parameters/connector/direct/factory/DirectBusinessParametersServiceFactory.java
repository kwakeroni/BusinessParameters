package be.kwakeroni.parameters.connector.direct.factory;

import be.kwakeroni.parameters.api.backend.BusinessParametersBackend;
import be.kwakeroni.parameters.api.backend.factory.BusinessParametersBackendFactory;
import be.kwakeroni.parameters.api.client.BusinessParameters;
import be.kwakeroni.parameters.api.client.factory.BusinessParametersFactory;
import be.kwakeroni.parameters.api.client.factory.ClientWireFormatterFactory;
import be.kwakeroni.parameters.connector.direct.BackendRegistry;
import be.kwakeroni.parameters.connector.direct.DirectBusinessParametersConnector;
import be.kwakeroni.parameters.connector.direct.WireFormatterRegistry;

import java.util.ServiceLoader;

/**
 * (C) 2016 Maarten Van Puymbroeck
 */
public class DirectBusinessParametersServiceFactory implements BusinessParametersFactory {

    @Override
    public BusinessParameters getInstance() {
        WireFormatterRegistry registry = new WireFormatterRegistry();
        registerFormatters(registry);
        BackendRegistry backends = new BackendRegistry();
        registerBackends(backends);
        return new DirectBusinessParametersConnector(registry, backends);
    }

    private void registerFormatters(WireFormatterRegistry registry){
        ServiceLoader<ClientWireFormatterFactory> loader = ServiceLoader.load(ClientWireFormatterFactory.class);
        for (ClientWireFormatterFactory factory : loader){
            factory.registerInstance(registry::register);
        }
    }

    private void registerBackends(BackendRegistry registry){
        ServiceLoader<BusinessParametersBackendFactory> loader = ServiceLoader.load(BusinessParametersBackendFactory.class);
        for (BusinessParametersBackendFactory factory : loader){
            BusinessParametersBackend backend = factory.getInstance();
            registry.register(backend);
        }
    }
}
