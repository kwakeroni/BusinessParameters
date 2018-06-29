package be.kwakeroni.parameters.backend.inmemory.factory;

import be.kwakeroni.evelyn.storage.impl.FileStorageProvider;
import be.kwakeroni.parameters.backend.api.BusinessParametersBackend;
import be.kwakeroni.parameters.backend.api.ConfigurationProvider;
import be.kwakeroni.parameters.backend.api.factory.BusinessParametersBackendFactory;
import be.kwakeroni.parameters.backend.inmemory.Config;
import be.kwakeroni.parameters.backend.inmemory.api.InMemoryGroupFactory;
import be.kwakeroni.parameters.backend.inmemory.api.InMemoryQuery;
import be.kwakeroni.parameters.backend.inmemory.fallback.TransientGroupDataStore;
import be.kwakeroni.parameters.backend.inmemory.persistence.PersistedGroupDataStore;
import be.kwakeroni.parameters.backend.inmemory.service.GroupDataStore;
import be.kwakeroni.parameters.backend.inmemory.service.InMemoryBackend;
import be.kwakeroni.parameters.definition.api.ParameterGroupDefinition;
import be.kwakeroni.parameters.definition.api.catalog.ParameterGroupDefinitionCatalog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.util.ServiceLoader;
import java.util.function.Supplier;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static be.kwakeroni.parameters.core.support.util.Reducers.atMostOne;

/**
 * (C) 2016 Maarten Van Puymbroeck
 */
public class InMemoryBackendServiceFactory implements BusinessParametersBackendFactory {

    private static final Logger LOG = LoggerFactory.getLogger(InMemoryBackendServiceFactory.class);

    static BusinessParametersBackend<InMemoryQuery<?>> INSTANCE = null;

    static Supplier<GroupDataStore> DATA_STORE_SUPPLIER = InMemoryBackendServiceFactory::getDefaultDataStoreSupplier;

    public static synchronized void setDataStoreSupplier(Supplier<GroupDataStore> supplier) {
        DATA_STORE_SUPPLIER = supplier;
    }

    @Override
    public BusinessParametersBackend<InMemoryQuery<?>> getInstance() {
        synchronized (InMemoryBackendServiceFactory.class) {
            if (INSTANCE == null) {
                INSTANCE = getInstance(DATA_STORE_SUPPLIER.get());
            }
        }
        return INSTANCE;
    }

    @SuppressWarnings("WeakerAccess")
    BusinessParametersBackend<InMemoryQuery<?>> getInstance(GroupDataStore dataStore) {
        return getInstance(loadFactories(), loadDefinitions(), dataStore);
    }

    @SuppressWarnings("WeakerAccess")
    BusinessParametersBackend<InMemoryQuery<?>> getInstance(
            InMemoryBackendGroupFactoryContext factories,
            Supplier<Stream<ParameterGroupDefinition<?>>> definitions,
            GroupDataStore dataStore) {
        return new InMemoryBackend(factories, definitions, dataStore);
    }

    private static InMemoryBackendGroupFactoryContext loadFactories() {
        ServiceLoader<InMemoryGroupFactory> loader = ServiceLoader.load(InMemoryGroupFactory.class);
        InMemoryBackendGroupFactoryContext context = new InMemoryBackendGroupFactoryContext();
        loader.forEach(context::register);
        return context;
    }

    private static Supplier<Stream<ParameterGroupDefinition<?>>> loadDefinitions() {
        return () -> loadServices(ParameterGroupDefinitionCatalog.class)
                .flatMap(ParameterGroupDefinitionCatalog::stream);
    }

    private static <S> Stream<S> loadServices(Class<S> serviceType) {
        ServiceLoader<S> services = ServiceLoader.load(serviceType);
        return StreamSupport.stream(services::spliterator, 0, false);
    }

    private static GroupDataStore getDefaultDataStoreSupplier() {
        return loadServices(ConfigurationProvider.class)
                .reduce(atMostOne())
                .map(ConfigurationProvider::getConfiguration)
                .flatMap(config -> config.get(Config.STORAGE_FOLDER))
                .map(InMemoryBackendServiceFactory::persistentStore)
                .orElseGet(InMemoryBackendServiceFactory::transientStore);
    }

    private static GroupDataStore persistentStore(Path location) {
        FileStorageProvider provider = new FileStorageProvider(location);
        PersistedGroupDataStore store = new PersistedGroupDataStore(provider);
        LOG.info("Using persistent data store at {}", location);
        return store;
    }

    private static GroupDataStore transientStore() {
        LOG.warn("Could not find location of persistent data store. Falling back on a transient in-memory store. Data will be lost at shutdown.");
        return new TransientGroupDataStore();
    }
}
