package be.kwakeroni.parameters.backend.es.service;

import be.kwakeroni.parameters.backend.es.api.ElasticSearchCriteria;
import be.kwakeroni.parameters.backend.es.api.ElasticSearchDataType;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * (C) 2017 Maarten Van Puymbroeck
 */
class DefaultElasticSearchCriteria implements ElasticSearchCriteria {

    private String group;
    private List<Criterion> parameterMatches = Collections.emptyList();
    private List<Criterion> parameterNotMatches = Collections.emptyList();
    private List<Criterion> parameterFilters = Collections.emptyList();

    public DefaultElasticSearchCriteria() {
    }

    @Override
    public void inGroup(String groupName) {
        if (group != null) throw new IllegalStateException("Group already specified in search criteria: " + group);
        this.group = groupName;
    }

    @Override
    public void addParameterMatch(String parameter, String value) {
        if (this.parameterMatches.isEmpty()) {
            this.parameterMatches = new ArrayList<>(1);
        }
        this.parameterMatches.add(new Match(parameter, value));
    }

    @Override
    public void addParameterNotMatch(String parameter, String value) {
        if (this.parameterNotMatches.isEmpty()) {
            this.parameterNotMatches = new ArrayList<>(1);
        }
        this.parameterNotMatches.add(new Match(parameter, value));
    }

    @Override
    public <T> void addParameterComparison(String parameter, ElasticSearchDataType<T> dataType, String operator, T value) {
        if (this.parameterFilters.isEmpty()) {
            this.parameterFilters = new ArrayList<>(1);
        }
        this.parameterFilters.add(new Comparison(parameter, dataType, operator, value));
    }

    @Override
    public void addComplexFilter(JSONObject filter) {
        if (this.parameterFilters.isEmpty()) {
            this.parameterFilters = new ArrayList<>(1);
        }
        this.parameterFilters.add(() -> filter);
    }

    @Override
    public JSONObject toJSONObject() {
        if (group == null) throw new IllegalStateException("Group not specified in search criteria");

        JSONObject filter;
        if ((parameterMatches == null || parameterMatches.isEmpty())
                && (parameterFilters == null || parameterFilters.isEmpty())
                && (parameterNotMatches == null || parameterNotMatches.isEmpty())) {
            filter = Match.match("_type", group);
        } else {

            JSONObject q = new JSONObject();
            {
                JSONArray matches = new JSONArray();
                matches.put(Match.match("_type", group));
                this.parameterMatches.forEach(match -> matches.put(match.toJSONObject()));
                q.put("must", matches);
            }

            if (isNotEmpty(this.parameterNotMatches)) {
                JSONArray notMatches = new JSONArray();
                this.parameterNotMatches.forEach(notMatch -> notMatches.put(notMatch.toJSONObject()));
                q.put("must_not", notMatches);
            }
            if (isNotEmpty(this.parameterFilters)) {
                JSONArray filters = new JSONArray();
                this.parameterFilters.forEach(f -> filters.put(f.toJSONObject()));
                q.put("filter", filters);
            }

            filter = new JSONObject().put("bool", q);
        }

        return
                new JSONObject().put("constant_score",
                        new JSONObject().put("filter", filter
                        ));


//            "query" : {
//                "constant_score" : {
//                    "filter" : {
//                        "bool" : {
//                            "should" : [
//                            { "term" : {"price" : 20}},
//                            { "term" : {"productID" : "XHDK-A-1293-#fJ3"}}
//                            ],
//                            "must_not" : {
//                                "term" : {"price" : 30}
//                            }
//                        }
//                    }
//                }
//            }
//        "query" : {
//            "constant_score" : {
//                "filter" : {
//                    "bool" : {
//                        "should" : [
//                        { "term" : {"productID" : "KDKE-B-9947-#kL5"}},
//                        { "bool" : {
//                            "must" : [
//                            { "term" : {"productID" : "JODL-X-1937-#pV7"}},
//                            { "term" : {"price" : 30}}
//                            ]
//                        }}
//                        ]
//                    }
//                }
//            }
    }

    private boolean isNotEmpty(Collection<?> coll) {
        return coll != null && !coll.isEmpty();
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

    private static final class Comparison<T> implements Criterion {
        public final String parameter;
        public final ElasticSearchDataType<T> dataType;
        public final T value;
        public final String op;

        public Comparison(String parameter, ElasticSearchDataType<T> dataType, String op, T value) {
            this.parameter = parameter;
            this.dataType = dataType;
            this.op = op;
            this.value = value;
        }

        public JSONObject toJSONObject() {
            return range(this.parameter, this.op, this.dataType.toJSONRepresentation(this.value));
        }

        public static JSONObject range(String parameter, String op, Object value) {
            return new JSONObject().put("range",
                    new JSONObject().put(parameter,
                            new JSONObject().put(op, value)));
        }

    }
}
