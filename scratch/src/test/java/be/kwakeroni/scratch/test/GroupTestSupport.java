package be.kwakeroni.scratch.test;

import be.kwakeroni.parameters.client.api.model.Entry;
import be.kwakeroni.parameters.client.api.model.EntryType;
import be.kwakeroni.parameters.client.api.model.Parameter;
import be.kwakeroni.parameters.client.api.model.ParameterGroup;
import be.kwakeroni.parameters.client.api.query.Query;
import be.kwakeroni.scratch.env.Environment;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

abstract class GroupTestSupport<ET extends EntryType> {
    private final Environment environment;
    protected final ParameterGroup<ET> group;
    protected final Parameter<?>[] parameters;
    protected final Object[] originalValues;
    protected final Object[] otherValues;

    public GroupTestSupport(Environment environment, ParameterGroup<ET> group, TestParameter<?>... parameters) {
        this.environment = environment;
        this.group = group;
        this.parameters = ExtArrays.map(parameters, p -> p.parameter, Parameter<?>[]::new);
        this.originalValues = ExtArrays.map(parameters, p -> p.originalValue);
        this.otherValues = ExtArrays.map(parameters, p -> p.otherValue);
    }


    protected static <T> TestParameter<T> param(Parameter<T> param, T originalValue, T otherValue) {
        return new TestParameter<>(param, originalValue, otherValue);
    }


    protected <T> T get(Query<ET, T> query) {
        Optional<T> optional = optGet(query);
        assertThat(optional).describedAs("[%s] No value returned for query %s", group.getName(), query).isNotEmpty();
        return optional.get();
    }

    protected <T> Optional<T> optGet(Query<ET, T> query) {
        return environment.getBusinessParameters().get(group, query);
    }

    protected <T> void set(Query<ET, T> query, T value) {
        environment.getWritableBusinessParameters().set(group, query, value);
    }

    protected void addEntry(Entry entry) {
        environment.getWritableBusinessParameters().addEntry(group, entry);
    }


    private <T> String toString(Parameter<T> param, Object value) {
        return param.toString((T) value);
    }

    protected <T> Parameter<T> parameter(int index) {
        return (Parameter<T>) parameters[index];
    }

    protected <T> T originalValue(int index) {
        return (T) originalValues[index];
    }

    protected <T> T otherValue(int index) {
        return (T) otherValues[index];
    }

    protected Entry entry(Object[] values) {
        return entry(parameters, values);
    }

    protected Entry entry(Parameter<?>[] params, Object[] values) {
        Map<String, String> map = IntStream.range(0, params.length).boxed()
                .collect(Collectors.collectingAndThen(
                        Collectors.toMap(i -> params[i].getName(), i -> toString(params[i], values[i])),
                        Collections::unmodifiableMap));
        return new Entry() {
            @Override
            public <T> T getValue(Parameter<T> parameter) {
                return parameter.fromString(map.get(parameter.getName()));
            }

            @Override
            public boolean hasValue(Parameter<?> parameter) {
                return map.containsKey(parameter.getName());
            }

            @Override
            public Map<String, String> toMap() {
                return map;
            }
        };
    }

}
