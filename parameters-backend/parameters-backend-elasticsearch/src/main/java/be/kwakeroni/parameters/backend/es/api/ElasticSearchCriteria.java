package be.kwakeroni.parameters.backend.es.api;

import org.json.JSONObject;

/**
 * (C) 2017 Maarten Van Puymbroeck
 */
public interface ElasticSearchCriteria {

    public void addParameterMatch(String parameter, String value);

    public JSONObject toJSONObject();

}
