package be.kwakeroni.parameters.backend.inmemory.factory;

import be.kwakeroni.parameters.backend.api.BusinessParametersBackend;
import be.kwakeroni.parameters.backend.api.factory.BusinessParametersBackendFactory;
import be.kwakeroni.parameters.backend.inmemory.api.InMemoryGroupFactory;
import be.kwakeroni.parameters.backend.inmemory.api.InMemoryQuery;
import be.kwakeroni.parameters.backend.inmemory.fallback.TransientGroupDataStore;
import be.kwakeroni.parameters.backend.inmemory.service.GroupDataStore;
import be.kwakeroni.parameters.backend.inmemory.service.InMemoryBackend;
import be.kwakeroni.parameters.definition.api.ParameterGroupDefinition;
import be.kwakeroni.parameters.definition.api.catalog.ParameterGroupDefinitionCatalog;

import java.util.ServiceLoader;
import java.util.function.Supplier;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * (C) 2016 Maarten Van Puymbroeck
 */
public class InMemoryBackendServiceFactory implements BusinessParametersBackendFactory {

    private static Supplier<GroupDataStore> DATA_STORE_SUPPLIER = TransientGroupDataStore::new;

    public static void setDataStoreSupplier(Supplier<GroupDataStore> supplier) {
        DATA_STORE_SUPPLIER = supplier;
    }

    @Override
    public BusinessParametersBackend<InMemoryQuery<?>> getInstance() {
        return getInstance(DATA_STORE_SUPPLIER.get());
    }

    public BusinessParametersBackend<InMemoryQuery<?>> getInstance(GroupDataStore dataStore) {
        return new InMemoryBackend(loadFactories(), loadDefinitions(), dataStore);
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

}
