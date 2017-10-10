package be.kwakeroni.parameters.core.support.service;

import java.util.ServiceLoader;
import java.util.function.Predicate;
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

    public static <T> T loadService(Class<T> interfaceType) {
        return loadService(interfaceType, t -> true);
    }

    public static <T> T loadService(Class<T> interfaceType, Predicate<? super T> filter) {
        T service = null;
        ServiceLoader<T> loader = ServiceLoader.load(interfaceType);
        for (T t : loader) {
            if (filter.test(t)) {
                if (service == null) {
                    service = t;
                } else {
                    throw new IllegalStateException(String.format("Expected one service of type %s, but found multiple: %s, %s", interfaceType.getName(), service, t));
                }
            }
        }

        if (service == null) {
            throw new IllegalStateException(String.format("Not found service of type %s", interfaceType.getName()));
        }

        return service;
    }

}
