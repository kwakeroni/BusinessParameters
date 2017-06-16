package be.kwakeroni.parameters.adapter.jmx;

import be.kwakeroni.parameters.backend.api.BusinessParametersBackend;

/**
 * Created by kwakeroni on 14/06/17.
 */
class JMXParameterGroupBackend implements JMXParameterGroupBackendMBean {

    private final BusinessParametersBackend<?> backend;

    public JMXParameterGroupBackend(BusinessParametersBackend<?> backend) {
        this.backend = backend;
    }


    @Override
    public int getGroupCount() {
        return backend.getGroupNames().size();
    }

}
