package be.kwakeroni.parameters.basic.wireformat.raw;

import be.kwakeroni.parameters.client.api.model.Parameter;
import be.kwakeroni.parameters.basic.client.model.Entry;

import java.util.Map;

/**
 * (C) 2017 Maarten Van Puymbroeck
 */
class DefaultEntry implements Entry {

    private final Map<String, String> values;

    DefaultEntry(Map<String, String> values) {
        this.values = values;
    }

    @Override
    public <T> T getValue(Parameter<T> parameter) {
        String value = this.values.get(parameter.getName());
        return (value == null)? null : parameter.fromString(value);
    }
}
