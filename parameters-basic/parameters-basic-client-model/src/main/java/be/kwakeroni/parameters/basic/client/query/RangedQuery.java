package be.kwakeroni.parameters.basic.client.query;

import be.kwakeroni.parameters.basic.client.connector.BasicExternalizer;
import be.kwakeroni.parameters.basic.client.model.Ranged;
import be.kwakeroni.parameters.client.api.externalize.ExternalizationContext;
import be.kwakeroni.parameters.client.api.query.PartialQuery;
import be.kwakeroni.parameters.client.api.query.Query;
import be.kwakeroni.parameters.api.client.model.EntryType;

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

    public String getValueString(){
        return type.apply(value);
    }

    public Query<ET, T> getSubQuery() {
        return subQuery;
    }

    @Override
    public Object externalize(ExternalizationContext context) {
        return context.getExternalizer(BasicExternalizer.class).externalizeRangedQuery(this, context);
    }

    @Override
    public String toString() {
        return "at(" + value + ")." + subQuery;
    }


    static class Partial<V, Missing extends EntryType>
            implements PartialQuery<Ranged<V, Missing>, Missing> {

        private final V value;
        private final Function<? super V, String> type;

        public Partial(V value, Function<? super V, String> type) {
            this.value = Objects.requireNonNull(value);
            this.type = Objects.requireNonNull(type);
        }

        @Override
        public <T> Query<Ranged<V, Missing>, T> andThen(Query<Missing, T> missingQuery) {
            return new RangedQuery<>(value, type, missingQuery);
        }

        public String toString(){
            return "entryAt("+value+") and then ?";
        }
    }
}
