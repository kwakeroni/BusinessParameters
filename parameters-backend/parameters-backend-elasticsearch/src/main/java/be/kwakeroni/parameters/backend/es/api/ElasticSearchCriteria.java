package be.kwakeroni.parameters.backend.es.api;

import org.json.JSONObject;

/**
 * (C) 2017 Maarten Van Puymbroeck
 */
public interface ElasticSearchCriteria {

    public void inGroup(String groupName);

    public void addParameterMatch(String parameter, String value);

    public void addParameterNotMatch(String parameter, String value);

    public <T> void addParameterComparison(String parameter, ElasticSearchDataType<T> dataType, String operator, T value);

    public void addComplexFilter(JSONObject filter);

    public JSONObject toJSONObject();

}
