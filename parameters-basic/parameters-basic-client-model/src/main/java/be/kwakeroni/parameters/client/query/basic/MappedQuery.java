package be.kwakeroni.parameters.client.query.basic;

import be.kwakeroni.parameters.client.api.Query;
import be.kwakeroni.parameters.client.model.EntryType;
import be.kwakeroni.parameters.client.model.basic.Mapped;

import java.util.Objects;

/**
 * (C) 2016 Maarten Van Puymbroeck
 */
public class MappedQuery<ET extends EntryType, T> implements Query<Mapped<ET>, T> {

    private final String key;
    private final Query<ET, T> subQuery;

    public MappedQuery(String key, Query<ET, T> subQuery) {
        this.key = Objects.requireNonNull(key, "key");
        this.subQuery = Objects.requireNonNull(subQuery, "subQuery");
    }

    @Override
    public String toString() {
        return "forKey(" + key + ")." + subQuery;
    }
}
