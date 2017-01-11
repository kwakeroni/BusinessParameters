package be.kwakeroni.parameters.basic.client.query;

import be.kwakeroni.parameters.client.api.model.Parameter;
import be.kwakeroni.parameters.client.api.query.ClientWireFormatterContext;
import be.kwakeroni.parameters.client.api.query.Query;
import be.kwakeroni.parameters.basic.client.model.Simple;

import java.util.Objects;

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
    public T internalizeResult(Object result, ClientWireFormatterContext context) {
        return context.getWireFormatter(BasicClientWireFormatter.class).internalizeValue(result, this, context);
    }

    @Override
    public String toString() {
        return "[" + parameter.getName() + "].value";
    }
}
