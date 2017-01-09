package be.kwakeroni.parameters.basic.client.query;

import be.kwakeroni.parameters.api.client.model.EntryType;
import be.kwakeroni.parameters.api.client.query.ExternalizationContext;
import be.kwakeroni.parameters.api.client.query.Query;
import be.kwakeroni.parameters.basic.client.model.Mapped;

import java.util.Objects;
import java.util.function.Function;

/**
 * (C) 2016 Maarten Van Puymbroeck
 */
public class MappedQuery<K, ET extends EntryType, T> implements Query<Mapped<K, ET>, T> {

    private final K key;
    private final Function<? super K, String> keyStringConverter;
    private final Query<ET, T> subQuery;

    public MappedQuery(K key, Function<? super K, String> keyStringConverter, Query<ET, T> subQuery) {
        this.key = Objects.requireNonNull(key, "key");
        this.keyStringConverter = Objects.requireNonNull(keyStringConverter, "keyStringConverter");
        this.subQuery = Objects.requireNonNull(subQuery, "subQuery");
    }

    public K getKey() {
        return key;
    }

    public String getKeyString() {
        return keyStringConverter.apply(key);
    }

    public Query<ET, T> getSubQuery() {
        return subQuery;
    }

    @Override
    public Object externalize(ExternalizationContext context) {
        return context.getExternalizer(BasicExternalizer.class).externalizeMappedQuery(this, context);
    }

    @Override
    public T internalize(Object result, ExternalizationContext context) {
        return this.subQuery.internalize(result, context);
    }

    @Override
    public String toString() {
        return "forKey(" + key + ")." + subQuery;
    }

}
