package be.kwakeroni.parameters.backend.inmemory.api;

import java.util.Map;

/**
 * (C) 2016 Maarten Van Puymbroeck
 */
public interface EntryData {

    public String getValue(String parameterName);

    public Map<String, String> asMap();

}
