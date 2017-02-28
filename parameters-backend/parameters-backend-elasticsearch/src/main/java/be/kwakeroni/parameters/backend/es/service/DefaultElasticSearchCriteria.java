package be.kwakeroni.parameters.backend.es.service;

import be.kwakeroni.parameters.backend.es.api.ElasticSearchCriteria;
import be.kwakeroni.parameters.backend.es.api.ElasticSearchDataType;
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
    private List<Criterion> parameterMatches = Collections.emptyList();
    private List<Criterion> parameterFilters = Collections.emptyList();

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
    public void addParameterComparison(String parameter, String operator, Object value) {
        if (this.parameterFilters.isEmpty()){
            this.parameterFilters = new ArrayList<>(1);
        }
        this.parameterFilters.add(new Comparison(parameter, operator, value));
    }

    @Override
    public JSONObject toJSONObject() {
        if ((parameterMatches == null || parameterMatches.isEmpty())
            && (parameterFilters == null || parameterFilters.isEmpty())) {
            return Match.match("_type", group);
        } else {
            JSONArray matches = new JSONArray();
            JSONArray filters = new JSONArray();

            matches.put(Match.match("_type", group));

            this.parameterMatches.forEach(match -> matches.put(match.toJSONObject()));
            this.parameterFilters.forEach(filter -> filters.put(filter.toJSONObject()));


            return new JSONObject().put("bool", new JSONObject()
                .put("must", matches)
                .put("filter", filters)
            );
        }
    }

    private static interface Criterion {
        public JSONObject toJSONObject();
    }

    private static final class Match implements Criterion {
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

    private static final class Comparison implements Criterion {
        public final String parameter;
        public final Object value;
        public final String op;

        public Comparison(String parameter, String op, Object value) {
            this.parameter = parameter;
            this.op = op;
            this.value = value;
        }

        public JSONObject toJSONObject() {
            return range(this.parameter, this.op, this.value);
        }

        public static JSONObject range(String parameter, String op, Object value) {
            return new JSONObject().put("range",
                    new JSONObject().put(parameter,
                            new JSONObject().put(op, value)));
        }

    }
}
