package be.kwakeroni.parameters.adapter.direct.factory;

import be.kwakeroni.parameters.adapter.direct.BackendRegistry;
import be.kwakeroni.parameters.adapter.direct.DirectBusinessParametersClient;
import be.kwakeroni.parameters.backend.api.BusinessParametersBackend;
import be.kwakeroni.parameters.backend.api.factory.BackendWireFormatterFactory;
import be.kwakeroni.parameters.backend.api.factory.BusinessParametersBackendFactory;
import be.kwakeroni.parameters.backend.api.query.BackendWireFormatterContext;
import be.kwakeroni.parameters.client.api.BusinessParameters;
import be.kwakeroni.parameters.client.api.WritableBusinessParameters;
import be.kwakeroni.parameters.client.api.factory.BusinessParametersFactory;
import be.kwakeroni.parameters.client.api.factory.ClientWireFormatterFactory;
import be.kwakeroni.parameters.core.support.backend.DefaultBackendWireFormatterContext;
import be.kwakeroni.parameters.core.support.client.DefaultClientWireFormatterContext;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.Set;
import java.util.function.Predicate;

import static be.kwakeroni.parameters.core.support.service.BusinessParameterServices.loadServices;
import static be.kwakeroni.parameters.core.support.util.Reducers.atMostOne;

/**
 * (C) 2016 Maarten Van Puymbroeck
 */
public class DirectBusinessParametersServiceFactory implements BusinessParametersFactory {

    public static final Set<String> SUPPORTED_WIREFORMATS = Collections.unmodifiableSet(new HashSet<>(Arrays.asList("raw")));

    private Predicate<? super BusinessParametersBackendFactory> backendFilter = all -> true;

    public void setBackendType(Predicate<? super BusinessParametersBackendFactory> backendFilter) {
        this.backendFilter = (backendFilter == null) ? all -> true : backendFilter;
    }

    @Override
    public BusinessParameters getInstance(Map<String, String> properties) {
        return getWritableInstance(properties);
    }

    @Override
    public WritableBusinessParameters getWritableInstance(Map<String, String> properties) {

        DefaultClientWireFormatterContext clientRegistry = new DefaultClientWireFormatterContext();
        loadServices(ClientWireFormatterFactory.class)
                .filter(factory -> SUPPORTED_WIREFORMATS.contains(factory.getWireFormat()))
                .forEach(clientRegistry::register);

        DefaultBackendWireFormatterContext backendRegistry = new DefaultBackendWireFormatterContext();
        loadServices(BackendWireFormatterFactory.class)
                .filter(factory -> SUPPORTED_WIREFORMATS.contains(factory.getWireFormat()))
                .forEach(backendRegistry::register);

        return new DirectBusinessParametersClient(getSingleBackend(), backendRegistry, clientRegistry);
    }

    private BusinessParametersBackend<?> getSingleBackend() {
        return loadServices(BusinessParametersBackendFactory.class)
                .filter(backendFilter::test)
                .reduce(atMostOne("Multiple backends are not supported. Multiple instances found: %s and %s"))
                .map(BusinessParametersBackendFactory::getInstance)
                .orElseThrow(() -> new IllegalStateException("No Business Parameters backend found"));

//        BusinessParametersBackend<?> result = null;
//
//        ServiceLoader<BusinessParametersBackendFactory> loader = ServiceLoader.load(BusinessParametersBackendFactory.class);
//        for (BusinessParametersBackendFactory factory : loader) {
//            if (backendFilter.test(factory)) {
//                if (result == null) {
//                    result = factory.getInstance();
//                } else {
//                    throw new IllegalStateException("Multiple backends are not supported");
//                }
//            }
//        }
//
//        if (result == null) {
//            throw new IllegalStateException("No Business Parameters backend found");
//        }

//        return result;
    }

    private BackendRegistry getAllBackends(BackendWireFormatterContext registry) {
        BackendRegistry backends = new BackendRegistry(registry);
        registerBackends(backends);
        return backends;
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
