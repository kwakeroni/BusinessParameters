package be.kwakeroni.parameters.core.support.service;

import java.util.ServiceLoader;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Created by kwakeroni on 19/06/17.
 */
public class BusinessParameterServices {

    private BusinessParameterServices() {
    }

    public static <T> Stream<T> loadServices(Class<T> interfaceType) {
        ServiceLoader<T> loader = ServiceLoader.load(interfaceType);
        return StreamSupport.stream(loader.spliterator(), false);
    }

}
