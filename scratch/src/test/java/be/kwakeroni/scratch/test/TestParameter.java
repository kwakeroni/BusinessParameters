package be.kwakeroni.scratch.test;

import be.kwakeroni.parameters.client.api.model.Parameter;

public class TestParameter<T> {
    final Parameter<T> parameter;
    final T originalValue;
    final T otherValue;

    public TestParameter(Parameter<T> parameter, T originalValue, T otherValue) {
        this.parameter = parameter;
        this.originalValue = originalValue;
        this.otherValue = otherValue;
    }
}
