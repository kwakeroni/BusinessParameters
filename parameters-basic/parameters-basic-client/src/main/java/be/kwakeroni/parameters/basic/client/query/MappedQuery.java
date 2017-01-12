package be.kwakeroni.parameters.basic.client.query;

import be.kwakeroni.parameters.client.api.model.EntryType;
import be.kwakeroni.parameters.client.api.query.ClientWireFormatterContext;
import be.kwakeroni.parameters.client.api.query.Query;
import be.kwakeroni.parameters.basic.client.model.Mapped;
import be.kwakeroni.parameters.types.api.ParameterType;

import java.util.Objects;
import java.util.function.Function;

/**
 * (C) 2016 Maarten Van Puymbroeck
 */
public class MappedQuery<K, ET extends EntryType, T> implements Query<Mapped<K, ET>, T> {

    private final K key;
    private final Function<? super K, String> keyStringConverter;
    private final Query<ET, T> subQuery;

    public MappedQuery(K key, ParameterType<K> keyType, Query<ET, T> subQuery){
        this(key, keyType::toString, subQuery);
    }

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
    public Object externalize(ClientWireFormatterContext context) {
        return context.getWireFormatter(BasicClientWireFormatter.class).externalizeMappedQuery(this, context);
    }

    @Override
    public T internalizeResult(Object result, ClientWireFormatterContext context) {
        return this.subQuery.internalizeResult(result, context);
    }

    @Override
    public String toString() {
        return "forKey(" + key + ")." + subQuery;
    }

}
