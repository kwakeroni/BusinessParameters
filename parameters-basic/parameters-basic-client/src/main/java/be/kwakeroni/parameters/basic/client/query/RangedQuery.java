package be.kwakeroni.parameters.basic.client.query;

import be.kwakeroni.parameters.basic.client.model.Ranged;
import be.kwakeroni.parameters.client.api.model.EntryType;
import be.kwakeroni.parameters.client.api.query.ClientWireFormatterContext;
import be.kwakeroni.parameters.client.api.query.PartialQuery;
import be.kwakeroni.parameters.client.api.query.Query;
import be.kwakeroni.parameters.types.api.ParameterType;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

/**
 * (C) 2016 Maarten Van Puymbroeck
 */
public class RangedQuery<V, ET extends EntryType, RangedType extends Ranged<V, ET>, T> implements Query<RangedType, T> {

    private final V value;
    private final Function<? super V, String> type;
    private final Query<ET, T> subQuery;

    public RangedQuery(V value, ParameterType<V> valueType, Query<ET, T> subQuery) {
        this(value, valueType::toString, subQuery);
    }

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
    public Object externalizeValue(T value, ClientWireFormatterContext context) {
        return this.subQuery.externalizeValue(value, context);
    }

    @Override
    public Optional<T> internalizeResult(Object result, ClientWireFormatterContext context) {
        return this.subQuery.internalizeResult(result, context);
    }

    @Override
    public String toString() {
        return "at(" + value + ")." + subQuery;
    }

    public static class Partial<V, ET extends EntryType, RangedType extends Ranged<V, ET>> implements PartialQuery<RangedType, ET> {
        private final V value;
        private final Function<? super V, String> type;

        public Partial(V value, ParameterType<V> type) {
            this(value, type::toString);
        }

        public Partial(V value, Function<? super V, String> type) {
            this.value = value;
            this.type = type;
        }

        @Override
        public <T> Query<RangedType, T> andThen(Query<ET, T> subQuery) {
            return new RangedQuery<>(value, type, subQuery);
        }

        public <T> Query<RangedType, T> _andThen(Query<ET, T> subQuery) {
            return new RangedQuery<>(value, type, subQuery);
        }
    }

}
