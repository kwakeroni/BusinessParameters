package be.kwakeroni.scratch;

import be.kwakeroni.parameters.backend.api.factory.BusinessParametersBackendFactory;

/**
 * (C) 2017 Maarten Van Puymbroeck
 */
public interface TestData extends AutoCloseable {

    public boolean acceptBackend(BusinessParametersBackendFactory factory);

    public boolean hasDataForGroup(String name);

    public void reset();

    public void notifyModifiedGroup(String name);
}
