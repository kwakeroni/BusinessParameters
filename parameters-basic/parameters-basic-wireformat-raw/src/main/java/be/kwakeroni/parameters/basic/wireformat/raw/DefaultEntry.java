package be.kwakeroni.parameters.basic.wireformat.raw;

import be.kwakeroni.parameters.basic.client.model.Entry;
import be.kwakeroni.parameters.client.api.model.Parameter;

import java.util.Collections;
import java.util.Map;

/**
 * (C) 2017 Maarten Van Puymbroeck
 */
class DefaultEntry implements Entry {

    private final Map<String, String> values;

    DefaultEntry(Map<String, String> values) {
        this.values = Collections.unmodifiableMap(values);
    }

    @Override
    public <T> T getValue(Parameter<T> parameter) {
        String value = this.values.get(parameter.getName());
        return (value == null) ? null : parameter.fromString(value);
    }

    @Override
    public boolean hasValue(Parameter<?> parameter) {
        return this.values.containsKey(parameter.getName());
    }

    @Override
    public Map<String, String> toMap() {
        return this.values;
    }
}
