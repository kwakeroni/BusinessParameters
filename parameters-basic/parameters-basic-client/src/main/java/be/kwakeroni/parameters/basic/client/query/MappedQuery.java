package be.kwakeroni.parameters.basic.client.query;

import be.kwakeroni.parameters.basic.client.model.Mapped;
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
public class MappedQuery<K, ET extends EntryType, MappedType extends Mapped<K, ET>, T> implements Query<MappedType, T> {

    private final K key;
    private final Function<? super K, String> keyStringConverter;
    private final Query<ET, T> subQuery;

    public MappedQuery(K key, ParameterType<K> keyType, Query<ET, T> subQuery) {
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
    public Object externalizeValue(T value, ClientWireFormatterContext context) {
        return this.subQuery.externalizeValue(value, context);
    }

    @Override
    public Optional<T> internalizeResult(Object result, ClientWireFormatterContext context) {
        return this.subQuery.internalizeResult(result, context);
    }

    @Override
    public String toString() {
        return "forKey(" + key + ")." + subQuery;
    }

    public static class Partial<K, ET extends EntryType, MappedType extends Mapped<K, ET>> implements PartialQuery<MappedType, ET> {

        private final K key;
        private final Function<? super K, String> keyStringConverter;

        public Partial(K key, ParameterType<K> keyType) {
            this(key, keyType::toString);
        }

        public Partial(K key, Function<? super K, String> keyStringConverter) {
            this.key = Objects.requireNonNull(key, "key");
            this.keyStringConverter = Objects.requireNonNull(keyStringConverter, "keyStringConverter");
        }

        @Override
        public <T> Query<MappedType, T> andThen(Query<ET, T> subQuery) {
            return new MappedQuery<>(key, keyStringConverter, subQuery);
        }
    }

}
