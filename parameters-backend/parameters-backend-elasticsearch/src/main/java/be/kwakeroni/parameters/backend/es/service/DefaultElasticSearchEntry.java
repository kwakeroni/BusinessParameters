package be.kwakeroni.parameters.backend.es.service;

import be.kwakeroni.parameters.backend.es.api.ElasticSearchEntry;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.collectingAndThen;

/**
 * (C) 2017 Maarten Van Puymbroeck
 */
final class DefaultElasticSearchEntry implements ElasticSearchEntry {
    private final JSONObject metadata;
    private JSONObject entry;
    private List<String> metaParams;

    public DefaultElasticSearchEntry(Map<String, String> map){
        this(new JSONObject()
                .put("_source", new JSONObject(map)));
    }

    public DefaultElasticSearchEntry(JSONObject elasticSearchResult) {
        this.entry = elasticSearchResult.getJSONObject("_source");
        this.metadata = elasticSearchResult;
//            this.entry.keys().forEachRemaining(key -> {
//                if (this.entry.optString())
//            });
    }

    private void addMetaParam(String name) {
        if (this.metaParams == null) {
            this.metaParams = new ArrayList<>(2);
        }
        this.metaParams.add(name);
    }

    private boolean isMetaParam(String name) {
        return this.metaParams != null && this.metaParams.contains(name);
    }

    @Override
    public String getId() {
        return metadata.getString("_id");
    }

    @Override
    public Map<String, String> getParameters() {
        return toParameterMap();
    }

    @Override
    public String getParameter(String parameter) {
        return entry.getString(parameter);
    }

    @Override
    public void setParameter(String parameter, String value) {
        entry.put(parameter, value);
    }

    @Override
    public void replace(Map<String, String> parameterMap) {
        this.entry = new JSONObject(parameterMap);
    }

    @Override
    public boolean hasParameter(String parameter) {
        return entry.has(parameter);
    }

    @Override
    public void clearParameter(String parameter) {
        entry.remove(parameter);
    }

    @Override
    public ElasticSearchEntry copy() {
        return new DefaultElasticSearchEntry(new JSONObject(this.metadata.toMap()));
    }

    @Override
    public Map<String, String> toParameterMap() {
        return entry.keySet()
                .stream()
                .filter(param -> !isMetaParam(param))
                .collect(collectingAndThen(
                        Collectors.toMap(identity(), (String key) -> entry.optString(key, null)),
                        Collections::unmodifiableMap));
    }

    @Override
    public void setMetaParameter(String parameter, Object value) {
        entry.put(parameter, value);
        addMetaParam(parameter);
    }

    @Override
    public Map<String, Object> toRawMap() {
        return entry.toMap();
    }

    public String toString(){
        return "("+getId()+")" + entry.toString(4);
    }
}
