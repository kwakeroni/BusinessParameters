package be.kwakeroni.parameters.backend.es.service;

import be.kwakeroni.parameters.backend.es.api.ElasticSearchCriteria;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * (C) 2017 Maarten Van Puymbroeck
 */
class DefaultElasticSearchCriteria implements ElasticSearchCriteria{

    private String group;
    private List<Match> parameterMatches = Collections.emptyList();

    public DefaultElasticSearchCriteria(String group) {
        this.group = group;
    }

    @Override
    public void addParameterMatch(String parameter, String value) {
        if (this.parameterMatches.isEmpty()){
            this.parameterMatches = new ArrayList<>(1);
        }
        this.parameterMatches.add(new Match(parameter, value));
    }

    @Override
    public JSONObject toJSONObject() {
        if (parameterMatches == null || parameterMatches.isEmpty()) {
            return Match.match("_type", group);
        } else {
            JSONArray criteria = new JSONArray();

            criteria.put(Match.match("_type", group));

            this.parameterMatches.forEach(match -> criteria.put(match.toJSONObject()));


            return new JSONObject().put("bool",
                new JSONObject().put("must", criteria)
            );
        }
    }

    private static final class Match {
        public final String parameter;
        public final String value;

        public Match(String parameter, String value) {
            this.parameter = parameter;
            this.value = value;
        }

        public JSONObject toJSONObject() {
            return match(this.parameter, this.value);
        }

        public static JSONObject match(String parameter, String value) {
            return new JSONObject().put("match",
                    new JSONObject().put(parameter, value));
        }

    }
}
