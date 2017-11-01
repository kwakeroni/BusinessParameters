package be.kwakeroni.parameters.adapter.rest.factory;

import be.kwakeroni.parameters.adapter.rest.RestBackendAdapter;
import be.kwakeroni.parameters.backend.api.BusinessParametersBackend;
import be.kwakeroni.parameters.backend.api.factory.BackendWireFormatterFactory;
import be.kwakeroni.parameters.backend.api.factory.BusinessParametersBackendFactory;
import be.kwakeroni.parameters.backend.api.query.BackendWireFormatterContext;
import be.kwakeroni.parameters.core.support.backend.DefaultBackendWireFormatterContext;

import java.util.function.Predicate;

import static be.kwakeroni.parameters.core.support.service.BusinessParameterServices.loadService;
import static be.kwakeroni.parameters.core.support.service.BusinessParameterServices.loadServices;

/**
 * Created by kwakeroni on 09/10/17.
 */
public class RestBackendAdapterFactory {

    private Predicate<? super BusinessParametersBackendFactory> backendFilter = all -> true;

    public void setBackendType(Predicate<? super BusinessParametersBackendFactory> backendFilter) {
        this.backendFilter = (backendFilter == null) ? all -> true : backendFilter;
    }

    public RestBackendAdapter newInstance() {
        BusinessParametersBackend<?> backend = loadBackend();
        BackendWireFormatterContext context = loadBackendFormatters();

        return new RestBackendAdapter(backend, context);
    }

    private BusinessParametersBackend<?> loadBackend() {
        return loadService(BusinessParametersBackendFactory.class, this.backendFilter).getInstance();
    }

    private BackendWireFormatterContext loadBackendFormatters() {
        DefaultBackendWireFormatterContext context = new DefaultBackendWireFormatterContext();
        loadServices(BackendWireFormatterFactory.class)
                .peek(factory -> System.out.println(factory.getWireFormat() + ": " + factory))
                .filter(factory -> "json".equals(factory.getWireFormat()))
                .forEach(context::register);
        return context;
    }

}
