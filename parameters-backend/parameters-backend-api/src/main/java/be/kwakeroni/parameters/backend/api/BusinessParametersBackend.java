package be.kwakeroni.parameters.backend.api;

import java.util.Collection;
import java.util.Map;

/**
 * (C) 2016 Maarten Van Puymbroeck
 */
public interface BusinessParametersBackend {

    public Object get(String group, Object query);

    public void set(String group, Object queryObject, Object value);

    public void addEntry(String group, Map<String, String> entry);

    public Collection<String> getGroupNames();
}
