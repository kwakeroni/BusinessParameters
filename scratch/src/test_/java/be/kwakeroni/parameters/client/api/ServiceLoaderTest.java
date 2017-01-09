package be.kwakeroni.parameters.client.api;

import be.kwakeroni.parameters.backend.inmemory.api.BusinessParametersBackend;
import be.kwakeroni.parameters.client.connector.QueryInternalizer;
import org.junit.Test;

import java.util.ServiceLoader;

/**
 * (C) 2016 Maarten Van Puymbroeck
 */
public class ServiceLoaderTest {

    @Test
    public void test(){
        testLoad("Backend", BusinessParametersBackend.class);
        testLoad("BackendWireFormatter", QueryInternalizer.class);
    }

    private <T> void testLoad(String name, Class<T> serviceType){
        ServiceLoader<T> backendLoader = ServiceLoader.load(serviceType);

        backendLoader.forEach(backend -> System.out.println(name + ": " + backend.getClass()));

    }

}
