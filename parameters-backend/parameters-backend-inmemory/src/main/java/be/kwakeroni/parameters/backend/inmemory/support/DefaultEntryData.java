package be.kwakeroni.parameters.backend.inmemory.support;

import be.kwakeroni.parameters.backend.inmemory.api.EntryData;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * (C) 2016 Maarten Van Puymbroeck
 */
public class DefaultEntryData implements EntryData {

    private final String id;
    private final Map<String, String> map;

    private DefaultEntryData(String id, Map<String, String> map) {
        this.id = id;
        this.map = map;
    }

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public Map<String, String> getParameters() {
        return Collections.unmodifiableMap(this.map);
    }

    @Override
    public String getValue(String parameterName) {
        return map.get(parameterName);
    }

    @Override
    public void setValue(String parameterName, String value) {
        if (this.map.containsKey(parameterName)) {
            this.map.put(parameterName, value);
        } else {
            throw new IllegalArgumentException("Unknown parameter: " + parameterName);
        }
    }

    public String toString() {
        return map.entrySet().stream().map(Object::toString).collect(Collectors.joining(System.lineSeparator(), "[" + System.lineSeparator(), "]"));
    }

    @Override
    public Map<String, String> asMap() {
        return Collections.unmodifiableMap(this.map);
    }

    public static DefaultEntryData of(Map<String, String> entry) {
        return of(UUID.randomUUID().toString(), entry);
    }

    public static DefaultEntryData of(String id, Map<String, String> entry) {
        return new DefaultEntryData(id, new HashMap<>(entry));
    }

}
