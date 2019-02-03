package be.kwakeroni.parameters.management.rest.factory;

import be.kwakeroni.parameters.backend.api.BusinessParametersBackend;
import be.kwakeroni.parameters.backend.api.factory.BusinessParametersBackendFactory;
import be.kwakeroni.parameters.definition.api.DefinitionVisitorContext;
import be.kwakeroni.parameters.definition.api.descriptor.DefinitionDescriptor;
import be.kwakeroni.parameters.definition.api.descriptor.DefinitionDescriptorBuilder;
import be.kwakeroni.parameters.management.rest.RestParameterManagement;

import java.util.function.Predicate;

import static be.kwakeroni.parameters.core.support.service.BusinessParameterServices.loadService;
import static be.kwakeroni.parameters.core.support.service.BusinessParameterServices.loadServices;

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
        DefinitionVisitorContext<DefinitionDescriptor> descriptorContext = loadDescriptors();

        return new RestParameterManagement(backend, descriptorContext);
    }

    private BusinessParametersBackend<?> loadBackend() {
        return loadService(BusinessParametersBackendFactory.class, this.backendFilter).getInstance();
    }

    private DefinitionVisitorContext<DefinitionDescriptor> loadDescriptors() {
        DefinitionDescriptorBuilderContext context = new DefinitionDescriptorBuilderContext();
        loadServices(DefinitionDescriptorBuilder.class)
                .forEach(builder -> builder.register(context::registerInstance));
        return context;
    }

}
