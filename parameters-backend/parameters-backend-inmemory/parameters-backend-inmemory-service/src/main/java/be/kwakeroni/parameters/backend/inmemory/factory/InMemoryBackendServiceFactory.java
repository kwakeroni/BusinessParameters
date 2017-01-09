package be.kwakeroni.parameters.backend.inmemory.factory;

import be.kwakeroni.parameters.api.backend.BusinessParametersBackend;
import be.kwakeroni.parameters.api.backend.factory.BusinessParametersBackendFactory;
import be.kwakeroni.parameters.api.backend.factory.InternalizerFactory;
import be.kwakeroni.parameters.api.backend.query.Internalizer;
import be.kwakeroni.parameters.backend.inmemory.service.DefaultInternalizationContext;
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
        ServiceLoader<InternalizerFactory> internalizerFactories = ServiceLoader.load(InternalizerFactory.class);

        DefaultInternalizationContext context = new DefaultInternalizationContext();
        internalizerFactories.forEach(factory -> factory.registerInstance(context::registerInternalizer));
        return new InMemoryBackend(context);
    }
}
