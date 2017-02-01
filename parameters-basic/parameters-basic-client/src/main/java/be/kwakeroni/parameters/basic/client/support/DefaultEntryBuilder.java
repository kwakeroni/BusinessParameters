package be.kwakeroni.parameters.basic.client.support;

import be.kwakeroni.parameters.client.api.model.Entry;
import be.kwakeroni.parameters.client.api.model.Parameter;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * (C) 2017 Maarten Van Puymbroeck
 */
class DefaultEntryBuilder implements Entries.Builder {

    private Map<Parameter<?>, Object> map;

    DefaultEntryBuilder(int capacity) {
        this.map = new HashMap<>(capacity);
    }

    <T> void put(Parameter<T> parameter, T value) {
        this.map.put(parameter, value);
    }

    private <T> T get(Parameter<T> parameter) {
        return (T) this.map.get(parameter);
    }

    private <T> String getString(Parameter<T> parameter) {
        return parameter.toString(get(parameter));
    }

    @Override
    public <T> Entries.Builder with(Parameter<T> parameter, T value) {
        put(parameter, value);
        return this;
    }

    @Override
    public Entry toEntry() {
        this.map = Collections.unmodifiableMap(this.map);

        return new Entry() {
            @Override
            public <T> T getValue(Parameter<T> parameter) {
                return get(parameter);
            }

            @Override
            public boolean hasValue(Parameter<?> parameter) {
                return map.containsKey(parameter);
            }

            @Override
            public Map<String, String> toMap() {
                return map.keySet()
                        .stream()
                        .collect(Collectors.toMap(Parameter::getName, parameter -> getString(parameter)));
            }
        };
    }
}
