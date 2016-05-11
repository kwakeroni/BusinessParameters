package be.kwakeroni.parameters.client.query.basic;

import be.kwakeroni.parameters.client.api.Query;
import be.kwakeroni.parameters.client.model.EntryType;
import be.kwakeroni.parameters.client.model.basic.Ranged;

import java.util.Objects;

/**
 * (C) 2016 Maarten Van Puymbroeck
 */
public class RangedQuery<V, ET extends EntryType, T> implements Query<Ranged<V, ET>, T> {

    private final V value;
    private final Query<ET, T> subQuery;

    public RangedQuery(V value, Query<ET, T> subQuery) {
        this.value = Objects.requireNonNull(value, "value");
        this.subQuery = Objects.requireNonNull(subQuery, "subQuery");
    }

    @Override
    public String toString() {
        return "at(" + value + ")." + subQuery;
    }
}
