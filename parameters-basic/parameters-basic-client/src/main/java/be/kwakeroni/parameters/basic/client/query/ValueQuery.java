package be.kwakeroni.parameters.basic.client.query;

import be.kwakeroni.parameters.basic.client.model.Simple;
import be.kwakeroni.parameters.client.api.model.Parameter;
import be.kwakeroni.parameters.client.api.query.ClientWireFormatterContext;
import be.kwakeroni.parameters.client.api.query.Query;

import java.util.Objects;
import java.util.Optional;

public class ValueQuery<T> implements Query<Simple, T> {

    private final Parameter<T> parameter;

    public ValueQuery(Parameter<T> parameter) {
        this.parameter = Objects.requireNonNull(parameter, "parameter");
    }

    public Parameter<T> getParameter() {
        return parameter;
    }

    @Override
    public Object externalize(ClientWireFormatterContext context) {
        return context.getWireFormatter(BasicClientWireFormatter.class).externalizeValueQuery(this, context);
    }

    @Override
    public Object externalizeValue(T value, ClientWireFormatterContext context) {
        return context.getWireFormatter(BasicClientWireFormatter.class).clientValueToWire(value, this, context);
    }

    @Override
    public Optional<T> internalizeResult(Object result, ClientWireFormatterContext context) {
        T value = context.getWireFormatter(BasicClientWireFormatter.class).wireToClientValue(result, this, context);
        return Optional.ofNullable(value);
    }

    @Override
    public String toString() {
        return "[" + parameter.getName() + "].value";
    }
}
