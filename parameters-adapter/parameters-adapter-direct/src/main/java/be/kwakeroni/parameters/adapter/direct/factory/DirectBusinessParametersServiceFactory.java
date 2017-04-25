package be.kwakeroni.parameters.adapter.direct.factory;

import be.kwakeroni.parameters.adapter.direct.BackendRegistry;
import be.kwakeroni.parameters.adapter.direct.DirectBusinessParametersClient;
import be.kwakeroni.parameters.backend.api.BusinessParametersBackend;
import be.kwakeroni.parameters.backend.api.factory.BackendWireFormatterFactory;
import be.kwakeroni.parameters.backend.api.factory.BusinessParametersBackendFactory;
import be.kwakeroni.parameters.client.api.BusinessParameters;
import be.kwakeroni.parameters.client.api.WritableBusinessParameters;
import be.kwakeroni.parameters.client.api.factory.BusinessParametersFactory;
import be.kwakeroni.parameters.client.api.factory.ClientWireFormatterFactory;

import java.util.ServiceLoader;
import java.util.function.Predicate;

/**
 * (C) 2016 Maarten Van Puymbroeck
 */
public class DirectBusinessParametersServiceFactory implements BusinessParametersFactory {

    private Predicate<? super BusinessParametersBackendFactory> backendFilter = all -> true;

    public void setBackendType(Predicate<? super BusinessParametersBackendFactory> backendFilter) {
        this.backendFilter = (backendFilter == null) ? all -> true : backendFilter;
    }

    @Override
    public BusinessParameters getInstance() {
        return getWritableInstance();
    }

    @Override
    public WritableBusinessParameters getWritableInstance() {
        DefaultClientWireFormatterContext clientRegistry = new DefaultClientWireFormatterContext();
        registerClientFormatters(clientRegistry);
        DefaultBackendWireFormatterContext backendRegistry = new DefaultBackendWireFormatterContext();
        registerBackendFormatters(backendRegistry);
        BackendRegistry backends = new BackendRegistry(backendRegistry);
        registerBackends(backends);
        return new DirectBusinessParametersClient(clientRegistry, backends);
    }

    private void registerClientFormatters(DefaultClientWireFormatterContext registry) {
        ServiceLoader<ClientWireFormatterFactory> loader = ServiceLoader.load(ClientWireFormatterFactory.class);
        loader.forEach(registry::register);
    }

    private void registerBackendFormatters(DefaultBackendWireFormatterContext registry) {
        ServiceLoader<BackendWireFormatterFactory> loader = ServiceLoader.load(BackendWireFormatterFactory.class);
        loader.forEach(registry::register);
    }

    private void registerBackends(BackendRegistry registry) {
        ServiceLoader<BusinessParametersBackendFactory> loader = ServiceLoader.load(BusinessParametersBackendFactory.class);
        for (BusinessParametersBackendFactory factory : loader) {
            if (backendFilter.test(factory)) {
                BusinessParametersBackend<?> backend = factory.getInstance();
                registry.register(backend);
            }
        }
    }
}
