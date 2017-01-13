package be.kwakeroni.parameters.backend.api;

import java.util.Collection;

/**
 * (C) 2016 Maarten Van Puymbroeck
 */
public interface BusinessParametersBackend {

    public Object get(String group, Object query);

    public void set(String group, Object queryObject, Object value);

    public Collection<String> getGroupNames();
}
