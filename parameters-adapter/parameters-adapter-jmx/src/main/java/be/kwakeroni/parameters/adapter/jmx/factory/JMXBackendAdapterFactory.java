package be.kwakeroni.parameters.adapter.jmx.factory;

import be.kwakeroni.parameters.adapter.jmx.JMXBackendAdapter;
import be.kwakeroni.parameters.adapter.jmx.JMXGroupMBeanFactoryContext;
import be.kwakeroni.parameters.adapter.jmx.api.JMXGroupMBeanFactory;
import be.kwakeroni.parameters.backend.api.BusinessParametersBackend;
import be.kwakeroni.parameters.backend.api.factory.BackendWireFormatterFactory;
import be.kwakeroni.parameters.backend.api.factory.BusinessParametersBackendFactory;
import be.kwakeroni.parameters.core.support.backend.DefaultBackendWireFormatterContext;
import be.kwakeroni.parameters.definition.api.ParameterGroupDefinition;
import be.kwakeroni.parameters.definition.api.catalog.ParameterGroupDefinitionCatalog;

import java.util.Iterator;
import java.util.ServiceLoader;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Created by kwakeroni on 04/05/17.
 */
public class JMXBackendAdapterFactory {


    private Predicate<? super BusinessParametersBackendFactory> backendFilter = all -> true;

    public void setBackendType(Predicate<? super BusinessParametersBackendFactory> backendFilter) {
        this.backendFilter = (backendFilter == null) ? all -> true : backendFilter;
    }

    public JMXBackendAdapter newInstance() {
        DefaultBackendWireFormatterContext wireFormatterContext = new DefaultBackendWireFormatterContext();
        registerBackendFormatters(wireFormatterContext);
        JMXBackendAdapter adapter = new JMXBackendAdapter(loadFactories(), wireFormatterContext);
        registerBackends(adapter);
        return adapter;
    }

    private void registerBackends(JMXBackendAdapter adapter) {
        ServiceLoader<BusinessParametersBackendFactory> loader = ServiceLoader.load(BusinessParametersBackendFactory.class);
        for (BusinessParametersBackendFactory factory : loader) {
            if (backendFilter.test(factory)) {
                BusinessParametersBackend<?> backend = factory.getInstance();
                adapter.register(backend);
            }
        }
    }

    private static JMXGroupMBeanFactoryContext loadFactories() {
        ServiceLoader<JMXGroupMBeanFactory> loader = ServiceLoader.load(JMXGroupMBeanFactory.class);
        JMXGroupMBeanFactoryContext context = new JMXGroupMBeanFactoryContext();
        loader.forEach(context::register);
        return context;
    }

    private void registerBackendFormatters(DefaultBackendWireFormatterContext registry) {
        ServiceLoader<BackendWireFormatterFactory> loader = ServiceLoader.load(BackendWireFormatterFactory.class);
        StreamSupport.stream(loader.spliterator(), false)
                .filter(factory -> "jmx".equals(factory.getWireFormat()))
                .forEach(registry::register);
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
