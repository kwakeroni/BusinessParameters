package be.kwakeroni.parameters.backend.inmemory.factory;

import be.kwakeroni.parameters.api.backend.BusinessParametersBackend;
import be.kwakeroni.parameters.api.backend.factory.BusinessParametersBackendFactory;
import be.kwakeroni.parameters.api.backend.factory.BackendWireFormatterFactory;
import be.kwakeroni.parameters.backend.inmemory.service.DefaultBackendWireFormatterContext;
import be.kwakeroni.parameters.backend.inmemory.service.InMemoryBackend;

import java.util.ServiceLoader;

/**
 * (C) 2016 Maarten Van Puymbroeck
 */
public class InMemoryBackendServiceFactory implements BusinessParametersBackendFactory {

    private static final InMemoryBackend INSTANCE = createInstance();

    @Override
    public BusinessParametersBackend getInstance() {
        return INSTANCE;
    }

    public static InMemoryBackend getSingletonInstance(){
        return INSTANCE;
    }

    private static InMemoryBackend createInstance(){
        ServiceLoader<BackendWireFormatterFactory> wireFormatterFactories = ServiceLoader.load(BackendWireFormatterFactory.class);

        DefaultBackendWireFormatterContext context = new DefaultBackendWireFormatterContext();
        wireFormatterFactories.forEach(factory -> factory.registerInstance(context::register));
        return new InMemoryBackend(context);
    }
}
