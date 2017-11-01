package be.kwakeroni.parameters.backend.es.api;

import be.kwakeroni.parameters.backend.api.BackendEntry;

import java.util.Map;

/**
 * (C) 2017 Maarten Van Puymbroeck
 */
public interface ElasticSearchEntry extends BackendEntry {

    public String getParameter(String parameter);

    public void setParameter(String parameter, String value);

    public void replace(Map<String, String> parameterMap);

    public boolean hasParameter(String parameter);

    public void clearParameter(String parameter);

    public Map<String, String> toParameterMap();

    public ElasticSearchEntry copy();

    public void setMetaParameter(String parameter, Object value);

    public Map<String, Object> toRawMap();
}
