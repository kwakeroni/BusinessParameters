package be.kwakeroni.parameters.basic.client.query;

import be.kwakeroni.parameters.api.client.model.EntryType;
import be.kwakeroni.parameters.api.client.query.ClientWireFormatterContext;
import be.kwakeroni.parameters.api.client.query.Query;
import be.kwakeroni.parameters.basic.client.model.Ranged;

import java.util.Objects;
import java.util.function.Function;

/**
 * (C) 2016 Maarten Van Puymbroeck
 */
public class RangedQuery<V, ET extends EntryType, T> implements Query<Ranged<V, ET>, T> {

    private final V value;
    private final Function<? super V, String> type;
    private final Query<ET, T> subQuery;

    public RangedQuery(V value, Function<? super V, String> type, Query<ET, T> subQuery) {
        this.value = Objects.requireNonNull(value, "value");
        this.type = Objects.requireNonNull(type, "type");
        this.subQuery = Objects.requireNonNull(subQuery, "subQuery");
    }

    public V getValue() {
        return value;
    }

    public String getValueString() {
        return type.apply(value);
    }

    public Query<ET, T> getSubQuery() {
        return subQuery;
    }

    @Override
    public Object externalize(ClientWireFormatterContext context) {
        return context.getWireFormatter(BasicClientWireFormatter.class).externalizeRangedQuery(this, context);
    }

    @Override
    public T internalizeResult(Object result, ClientWireFormatterContext context) {
        return this.subQuery.internalizeResult(result, context);
    }

    @Override
    public String toString() {
        return "at(" + value + ")." + subQuery;
    }

}
