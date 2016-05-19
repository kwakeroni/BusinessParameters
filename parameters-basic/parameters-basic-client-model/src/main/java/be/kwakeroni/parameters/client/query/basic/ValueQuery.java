package be.kwakeroni.parameters.client.query.basic;

import be.kwakeroni.parameters.client.api.externalize.ExternalizationContext;
import be.kwakeroni.parameters.client.api.query.Query;
import be.kwakeroni.parameters.client.basic.external.BasicExternalizer;
import be.kwakeroni.parameters.client.model.Parameter;
import be.kwakeroni.parameters.client.model.basic.Simple;

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
    public Object externalize(ExternalizationContext context) {
        return context.getExternalizer(BasicExternalizer.class).externalizeValueQuery(this, context);
    }


    @Override
    public String toString() {
        return "[" + parameter.getName() + "].value";
    }
}
