package be.kwakeroni.parameters.basic.client.query;

import be.kwakeroni.parameters.api.client.model.EntryType;
import be.kwakeroni.parameters.api.client.model.Parameter;
import be.kwakeroni.parameters.api.client.query.Query;
import be.kwakeroni.parameters.basic.client.model.Entry;
import be.kwakeroni.parameters.basic.client.model.Mapped;
import be.kwakeroni.parameters.basic.client.model.Ranged;
import be.kwakeroni.parameters.basic.client.model.Simple;
import be.kwakeroni.parameters.client.api.query.PartialQuery;

import java.util.function.Function;

/**
 * (C) 2016 Maarten Van Puymbroeck
 */
public class BasicQueries {

    private BasicQueries() {

    }

    public static <V, Missing extends EntryType> PartialQuery<Ranged<V, Missing>, Missing> entryAt(V value, Function<? super V, String> type) {
        return null; //new RangedQuery.Partial<>(value, type);
    }

    public static <K, Missing extends EntryType> PartialQuery<Mapped<K, Missing>, Missing> forKey(K key, Function<? super K, String> type) {
        return null; //new MappedQuery.Partial<>(key, type);
    }

    public static <P> Query<Simple, P> valueOf(Parameter<P> parameter) {
        return new ValueQuery<>(parameter);
    }

    public static Query<Simple, Entry> entry() {
        return new EntryQuery();
    }
}
