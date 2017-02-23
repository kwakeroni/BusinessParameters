package be.kwakeroni.scratch;

import be.kwakeroni.parameters.backend.api.BusinessParametersBackend;

/**
 * (C) 2017 Maarten Van Puymbroeck
 */
public interface TestData extends AutoCloseable {

    public boolean hasDataForGroup(String name);
    public void reset();

}
