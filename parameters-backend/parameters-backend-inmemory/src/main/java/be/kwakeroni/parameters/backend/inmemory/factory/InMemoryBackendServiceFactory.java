package be.kwakeroni.parameters.backend.inmemory.factory;

import be.kwakeroni.evelyn.storage.impl.FileStorageProvider;
import be.kwakeroni.parameters.backend.api.BusinessParametersBackend;
import be.kwakeroni.parameters.backend.api.factory.BusinessParametersBackendFactory;
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

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.Properties;
import java.util.ServiceLoader;
import java.util.function.Supplier;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * (C) 2016 Maarten Van Puymbroeck
 */
public class InMemoryBackendServiceFactory implements BusinessParametersBackendFactory {

    private static final Logger LOG = LoggerFactory.getLogger(InMemoryBackendServiceFactory.class);

    static Supplier<GroupDataStore> DATA_STORE_SUPPLIER = InMemoryBackendServiceFactory::getDefaultDataStoreSupplier;

    public static void setDataStoreSupplier(Supplier<GroupDataStore> supplier) {
        DATA_STORE_SUPPLIER = supplier;
    }

    @Override
    public BusinessParametersBackend<InMemoryQuery<?>> getInstance() {
        return getInstance(DATA_STORE_SUPPLIER.get());
    }

    public BusinessParametersBackend<InMemoryQuery<?>> getInstance(GroupDataStore dataStore) {
        return getInstance(loadFactories(), loadDefinitions(), dataStore);
    }

    BusinessParametersBackend<InMemoryQuery<?>> getInstance(InMemoryBackendGroupFactoryContext factories, Supplier<Stream<ParameterGroupDefinition<?>>> definitions, GroupDataStore dataStore) {
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
        return getConfigurationProperties()
                .map(props -> props.getProperty("inmemory.storage.folder"))
                .map(Paths::get)
                .map(InMemoryBackendServiceFactory::persistentStore)
                .orElseGet(InMemoryBackendServiceFactory::transientStore);
    }

    private static Optional<Properties> getConfigurationProperties() {
        URL url = Thread.currentThread().getContextClassLoader().getResource("business-parameters.properties");
        return Optional.ofNullable(url)
                .map(InMemoryBackendServiceFactory::load);
    }

    private static Properties load(URL url) {
        try {
            Properties props = new Properties();
            props.load(url.openStream());
            return props;
        } catch (IOException exc) {
            throw new UncheckedIOException(exc);
        }
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
