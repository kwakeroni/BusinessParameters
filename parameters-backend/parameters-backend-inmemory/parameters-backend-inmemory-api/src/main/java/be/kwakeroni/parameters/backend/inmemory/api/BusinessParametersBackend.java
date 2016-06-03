package be.kwakeroni.parameters.backend.inmemory.api;

/**
 * (C) 2016 Maarten Van Puymbroeck
 */
public interface BusinessParametersBackend {

    Object get(String group, Object query);

}
