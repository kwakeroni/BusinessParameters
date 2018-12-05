package be.kwakeroni.parameters.basic.wireformat.json;

import be.kwakeroni.parameters.client.api.model.Entry;
import be.kwakeroni.parameters.client.api.model.Parameter;

import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * (C) 2017 Maarten Van Puymbroeck
 */
class DefaultEntry implements Entry {

    private final Map<String, Object> values;

    DefaultEntry(Map<String, Object> values) {
        this.values = Collections.unmodifiableMap(values);
    }

    @Override
    public <T> T getValue(Parameter<T> parameter) {
        Object value = this.values.get(parameter.getName());
        return (value == null) ? null : parameter.fromString(String.valueOf(value));
    }

    @Override
    public boolean hasValue(Parameter<?> parameter) {
        return this.values.containsKey(parameter.getName());
    }

    @Override
    public Map<String, String> toMap() {
        return this.values.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, e -> String.valueOf(e.getValue())));
    }

    public String toString() {
        return values.toString();
    }
}
