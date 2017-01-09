package be.kwakeroni.parameters.connector.direct.factory;

import be.kwakeroni.parameters.api.backend.BusinessParametersBackend;
import be.kwakeroni.parameters.api.backend.factory.BusinessParametersBackendFactory;
import be.kwakeroni.parameters.api.client.BusinessParameters;
import be.kwakeroni.parameters.api.client.factory.BusinessParametersFactory;
import be.kwakeroni.parameters.api.client.factory.ExternalizerFactory;
import be.kwakeroni.parameters.api.client.query.Externalizer;
import be.kwakeroni.parameters.connector.direct.BackendRegistry;
import be.kwakeroni.parameters.connector.direct.DirectBusinessParametersConnector;
import be.kwakeroni.parameters.connector.direct.ExternalizerRegistry;

import java.util.Iterator;
import java.util.ServiceLoader;
import java.util.function.BiConsumer;

/**
 * (C) 2016 Maarten Van Puymbroeck
 */
public class DirectBusinessParametersServiceFactory implements BusinessParametersFactory {

    @Override
    public BusinessParameters getInstance() {
        ExternalizerRegistry registry = new ExternalizerRegistry();
        registerExternalizers(registry);
        BackendRegistry backends = new BackendRegistry();
        registerBackends(backends);
        return new DirectBusinessParametersConnector(registry, backends);
    }

    private void registerExternalizers(ExternalizerRegistry registry){
        ServiceLoader<ExternalizerFactory> loader = ServiceLoader.load(ExternalizerFactory.class);
        for (ExternalizerFactory factory : loader){
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
