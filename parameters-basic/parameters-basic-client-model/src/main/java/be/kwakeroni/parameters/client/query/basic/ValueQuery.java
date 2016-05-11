package be.kwakeroni.parameters.client.query.basic;

import be.kwakeroni.parameters.client.api.Query;
import be.kwakeroni.parameters.client.model.Parameter;
import be.kwakeroni.parameters.client.model.basic.Simple;

import java.util.Objects;

public class ValueQuery<T> implements Query<Simple, T> {

    private final Parameter<T> parameter;

    public ValueQuery(Parameter<T> parameter) {
        this.parameter = Objects.requireNonNull(parameter, "parameter");
    }

    @Override
    public String toString() {
        return "[" + parameter.getName() + "].value";
    }
}
