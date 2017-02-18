package be.kwakeroni.parameters.backend.inmemory.factory;

import be.kwakeroni.parameters.backend.api.BusinessParametersBackend;
import be.kwakeroni.parameters.backend.api.factory.BusinessParametersBackendFactory;
import be.kwakeroni.parameters.backend.inmemory.api.InMemoryQuery;
import be.kwakeroni.parameters.backend.inmemory.service.InMemoryBackend;

/**
 * (C) 2016 Maarten Van Puymbroeck
 */
public class InMemoryBackendServiceFactory implements BusinessParametersBackendFactory {

    private static final InMemoryBackend INSTANCE = new InMemoryBackend();

    @Override
    public BusinessParametersBackend<InMemoryQuery<?>> getInstance() {
        return INSTANCE;
    }

    public static InMemoryBackend getSingletonInstance() {
        return INSTANCE;
    }

}
