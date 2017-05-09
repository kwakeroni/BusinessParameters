package be.kwakeroni.parameters.backend.es.factory;

import be.kwakeroni.parameters.backend.api.BusinessParametersBackend;
import be.kwakeroni.parameters.backend.api.factory.BusinessParametersBackendFactory;
import be.kwakeroni.parameters.backend.es.api.ElasticSearchGroupFactory;
import be.kwakeroni.parameters.backend.es.api.ElasticSearchQuery;
import be.kwakeroni.parameters.backend.es.service.Configuration;
import be.kwakeroni.parameters.backend.es.service.ElasticSearchBackend;
import be.kwakeroni.parameters.definition.api.ParameterGroupDefinition;
import be.kwakeroni.parameters.definition.api.catalog.ParameterGroupDefinitionCatalog;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.Iterator;
import java.util.Properties;
import java.util.ServiceLoader;
import java.util.function.Supplier;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * (C) 2017 Maarten Van Puymbroeck
 */
public class ElasticSearchBackendServiceFactory implements BusinessParametersBackendFactory {

    public static final String CONFIG_FILE = "/parameters-backend-elasticsearch.properties";

    @Override
    public BusinessParametersBackend<ElasticSearchQuery<?>> getInstance() {
        return getSingletonInstance();
        // @todo remove singleton
//        return new ElasticSearchBackend(loadConfig());
    }

    private static Configuration loadConfig() {
        InputStream config = Configuration.class.getResourceAsStream(CONFIG_FILE);

        if (config == null) {
            throw new IllegalStateException("ElasticSearch configuration incomplete: Config file not found: " + CONFIG_FILE);
        }

        Properties properties = new Properties();
        try {
            properties.load(config);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }

        return new Configuration(properties);
    }

    private static ElasticSearchBackend INSTANCE;

    public static synchronized ElasticSearchBackend getSingletonInstance() {
        if (INSTANCE == null) {
            INSTANCE = createNewInstance();
        }
        return INSTANCE;
    }

    private static ElasticSearchBackend createNewInstance() {
        return new ElasticSearchBackend(loadConfig(), loadFactories(), loadDefinitions());
    }

    private static ElasticSearchGroupFactoryContext loadFactories() {
        ServiceLoader<ElasticSearchGroupFactory> loader = ServiceLoader.load(ElasticSearchGroupFactory.class);
        ElasticSearchGroupFactoryContext context = new ElasticSearchGroupFactoryContext();
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
