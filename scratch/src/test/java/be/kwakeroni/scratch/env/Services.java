package be.kwakeroni.scratch.env;

import be.kwakeroni.parameters.definition.api.ParameterGroupDefinition;
import be.kwakeroni.parameters.definition.api.catalog.ParameterGroupDefinitionCatalog;

import java.util.Iterator;
import java.util.Optional;
import java.util.ServiceLoader;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Created by kwakeroni on 26/04/17.
 */
public class Services {

    private Services() {

    }

    public static Optional<ParameterGroupDefinition> loadDefinition(String name) {
        return loadDefinitions()
                .filter(definition -> name.equals(definition.getName()))
                .findAny();
    }

    public static Stream<ParameterGroupDefinition> loadDefinitions() {
        return loadServices(ParameterGroupDefinitionCatalog.class)
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
