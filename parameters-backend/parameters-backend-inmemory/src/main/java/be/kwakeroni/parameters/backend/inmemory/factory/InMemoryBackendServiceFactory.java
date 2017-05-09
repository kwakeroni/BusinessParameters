package be.kwakeroni.parameters.backend.inmemory.factory;

import be.kwakeroni.parameters.backend.api.BusinessParametersBackend;
import be.kwakeroni.parameters.backend.api.factory.BusinessParametersBackendFactory;
import be.kwakeroni.parameters.backend.inmemory.api.InMemoryGroupFactory;
import be.kwakeroni.parameters.backend.inmemory.api.InMemoryQuery;
import be.kwakeroni.parameters.backend.inmemory.service.InMemoryBackend;
import be.kwakeroni.parameters.definition.api.ParameterGroupDefinition;
import be.kwakeroni.parameters.definition.api.catalog.ParameterGroupDefinitionCatalog;

import java.util.Iterator;
import java.util.ServiceLoader;
import java.util.function.Supplier;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * (C) 2016 Maarten Van Puymbroeck
 */
public class InMemoryBackendServiceFactory implements BusinessParametersBackendFactory {

    private static InMemoryBackend INSTANCE;

    @Override
    public BusinessParametersBackend<InMemoryQuery<?>> getInstance() {
        return INSTANCE;
    }

    public static synchronized InMemoryBackend getSingletonInstance() {
        if (INSTANCE == null) {
            INSTANCE = createNewInstance();
        }
        return INSTANCE;
    }

    private static InMemoryBackend createNewInstance() {
        return new InMemoryBackend(loadFactories(), loadDefinitions());
    }

    private static InMemoryBackendGroupFactoryContext loadFactories() {
        ServiceLoader<InMemoryGroupFactory> loader = ServiceLoader.load(InMemoryGroupFactory.class);
        InMemoryBackendGroupFactoryContext context = new InMemoryBackendGroupFactoryContext();
        loader.forEach(factory -> context.register(factory.getProvidedInterface(), factory));
        return context;
    }

    private static Supplier<Stream<ParameterGroupDefinition>> loadDefinitions() {
        return () -> loadServices(ParameterGroupDefinitionCatalog.class)
                .flatMap(ParameterGroupDefinitionCatalog::stream);
    }

    public static <S> Stream<S> loadServices(Class<S> serviceType) {
        ServiceLoader<S> services = ServiceLoader.load(serviceType);
        return StreamSupport.stream(services::spliterator, 0, false);
    }


    public static <S> S loadService(Class<S> serviceType) {
        ServiceLoader<S> loader = ServiceLoader.load(serviceType);
        Iterator<S> services = loader.iterator();
        if (!services.hasNext()) {
            throw new IllegalStateException("Service not found: " + serviceType.getName());
        }
        S service = services.next();
        if (services.hasNext()) {
            throw new IllegalStateException("Multiple services of type " + serviceType.getName() + ": " + service.getClass().getName() + " & " + services.next().getClass().getName());
        }
        return service;
    }
}
