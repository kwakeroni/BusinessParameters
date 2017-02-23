package be.kwakeroni.parameters.backend.es.api;

import org.json.JSONObject;

import java.util.stream.Stream;

/**
 * (C) 2017 Maarten Van Puymbroeck
 */
public interface ElasticSearchData {

    Stream<JSONObject> query(JSONObject query);

}
