package be.kwakeroni.parameters.management.rest.factory;

import be.kwakeroni.parameters.backend.api.BusinessParametersBackend;
import be.kwakeroni.parameters.backend.api.factory.BusinessParametersBackendFactory;
import be.kwakeroni.parameters.management.rest.RestParameterManagement;

import java.util.function.Predicate;

import static be.kwakeroni.parameters.core.support.service.BusinessParameterServices.loadService;

/**
 * Created by kwakeroni on 11/10/17.
 */
public class RestParameterManagementFactory {

    private Predicate<? super BusinessParametersBackendFactory> backendFilter = all -> true;

    public void setBackendType(Predicate<? super BusinessParametersBackendFactory> backendFilter) {
        this.backendFilter = (backendFilter == null) ? all -> true : backendFilter;
    }

    public RestParameterManagement newInstance() {
        BusinessParametersBackend<?> backend = loadBackend();

        return new RestParameterManagement(backend);
    }

    private BusinessParametersBackend<?> loadBackend() {
        return loadService(BusinessParametersBackendFactory.class, this.backendFilter).getInstance();
    }

}
