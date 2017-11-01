package be.kwakeroni.parameters.backend.inmemory.api;

import be.kwakeroni.parameters.backend.api.BackendEntry;

import java.util.Map;

/**
 * (C) 2016 Maarten Van Puymbroeck
 */
public interface EntryData extends BackendEntry {

    public String getValue(String parameterName);

    public void setValue(String parameterName, String value);

    public Map<String, String> asMap();
}
