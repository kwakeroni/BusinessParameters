package be.kwakeroni.parameters.adapter.jmx.factory;

import be.kwakeroni.parameters.adapter.jmx.JMXBackendAdapter;
import be.kwakeroni.parameters.backend.api.BusinessParametersBackend;
import be.kwakeroni.parameters.backend.api.factory.BusinessParametersBackendFactory;

import java.util.ServiceLoader;
import java.util.function.Predicate;

/**
 * Created by kwakeroni on 04/05/17.
 */
public class JMXBackendAdapterFactory {


    private Predicate<? super BusinessParametersBackendFactory> backendFilter = all -> true;

    public void setBackendType(Predicate<? super BusinessParametersBackendFactory> backendFilter) {
        this.backendFilter = (backendFilter == null) ? all -> true : backendFilter;
    }

    public JMXBackendAdapter newInstance(){
        JMXBackendAdapter adapter = new JMXBackendAdapter();
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
}
