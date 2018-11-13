package be.kwakeroni.parameters.backend.inmemory.factory;

import be.kwakeroni.evelyn.storage.impl.FileStorageProvider;
import be.kwakeroni.parameters.backend.api.BusinessParametersBackend;
import be.kwakeroni.parameters.backend.api.Configuration;
import be.kwakeroni.parameters.backend.api.ConfigurationProvider;
import be.kwakeroni.parameters.backend.api.factory.BusinessParametersBackendFactory;
import be.kwakeroni.parameters.backend.inmemory.Config;
import be.kwakeroni.parameters.backend.inmemory.api.InMemoryGroupFactory;
import be.kwakeroni.parameters.backend.inmemory.api.InMemoryQuery;
import be.kwakeroni.parameters.backend.inmemory.fallback.TransientGroupDataStore;
import be.kwakeroni.parameters.backend.inmemory.persistence.PersistedGroupDataStore;
import be.kwakeroni.parameters.backend.inmemory.service.GroupDataStore;
import be.kwakeroni.parameters.backend.inmemory.service.InMemoryBackend;
import be.kwakeroni.parameters.core.support.util.function.ThrowingFunction;
import be.kwakeroni.parameters.definition.api.ParameterGroupDefinition;
import be.kwakeroni.parameters.definition.api.catalog.ParameterGroupDefinitionCatalog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Optional;
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

    private static <S> Stream<S> loadServices(Class<S> serviceType) {
        return stream(ServiceLoader.load(serviceType));
    }

    private static <S> Stream<S> stream(ServiceLoader<S> serviceLoader) {
        return StreamSupport.stream(serviceLoader::spliterator, 0, false);
    }

    private static Optional<Configuration> getConfiguration() {
        return loadServices(ConfigurationProvider.class)
                .reduce(atMostOne())
                .map(ConfigurationProvider::getConfiguration);

    }

    private static GroupDataStore getDefaultDataStoreSupplier() {
        return getConfiguration()
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


    private static Supplier<Stream<ParameterGroupDefinition<?>>> loadDefinitions() {

        Optional<Path> definitionsLocation = getConfiguration()
                .flatMap(config -> config.get(Config.DEFINITIONS_FOLDER));

        LOG.info(definitionsLocation.map(path -> String.format("Adding definitions from %s", path)).orElse("Using definitions on basic classpath only"));

        ServiceLoader<ParameterGroupDefinitionCatalog> serviceLoader = definitionsLocation
                .map(InMemoryBackendServiceFactory::getClassLoaderFromLocation)
                .map(parentLoader -> ServiceLoader.load(ParameterGroupDefinitionCatalog.class, parentLoader))
                .orElseGet(() -> ServiceLoader.load(ParameterGroupDefinitionCatalog.class));

        return () -> stream(serviceLoader)
                .flatMap(ParameterGroupDefinitionCatalog::stream)
                .peek(definition -> LOG.info("Detected definition {}", definition.getName()));
    }

    private static ClassLoader getClassLoaderFromLocation(Path path) {
        URL[] urls = getJarFiles(path)
                .peek(p -> LOG.info("Registering jar-file for definitions: {}", p))
                .map(Path::toUri)
                .map(ThrowingFunction.unchecked(URI::toURL, UncheckedIOException::new))
                .toArray(URL[]::new);

        return new URLClassLoader(urls, Thread.currentThread().getContextClassLoader());
    }

    private static Stream<Path> getJarFiles(Path path) {
        try {
            return Files.find(path, 1, InMemoryBackendServiceFactory::isJarFile);
        } catch (IOException exc) {
            throw new UncheckedIOException(exc);
        }
    }

    private static boolean isJarFile(Path path, BasicFileAttributes atts) {
//        LOG.info("is jar file '{}'? {} {}", path, (!atts.isDirectory()) , path.getFileName().toString().endsWith(".jar"));
        return (!atts.isDirectory()) && path.getFileName().toString().endsWith(".jar");
    }
}
