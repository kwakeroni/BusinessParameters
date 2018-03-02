package be.kwakeroni.parameters.backend.inmemory.support;

import be.kwakeroni.parameters.backend.inmemory.api.EntryData;
import org.json.JSONObject;

import java.util.Collections;
import java.util.Map;
import java.util.UUID;

public class JsonEntryData implements EntryData {

    private final String id;
    private final JSONObject jsonObject;

    private JsonEntryData(String id, JSONObject jsonObject) {
        this.id = id;
        this.jsonObject = jsonObject;
    }

    @Override
    public String getValue(String parameterName) {
        return jsonObject.getString(parameterName);
    }

    @Override
    public void setValue(String parameterName, String value) {
        if (this.jsonObject.opt(parameterName) == null) {
            throw new IllegalArgumentException("Unknown parameter: " + parameterName);
        } else {
            jsonObject.put(parameterName, value);
        }
    }

    @Override
    public Map<String, String> asMap() {
        return Collections.unmodifiableMap((Map<String, String>) (Map<String, ?>) jsonObject.toMap());
    }

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public Map<String, String> getParameters() {
        return asMap();
    }

    public String toJsonString() {
        return this.jsonObject.toString();
    }

    @Override
    public String toString() {
        return this.jsonObject.toString(2);
    }

    public static JsonEntryData of(JSONObject jsonObject) {
        return of(UUID.randomUUID().toString(), jsonObject);
    }

    public static JsonEntryData of(String id, JSONObject jsonObject) {
        return new JsonEntryData(id, jsonObject);
    }


}
