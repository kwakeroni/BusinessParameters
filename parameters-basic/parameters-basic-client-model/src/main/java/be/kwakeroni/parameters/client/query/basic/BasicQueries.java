package be.kwakeroni.parameters.client.query.basic;

import be.kwakeroni.parameters.client.api.entry.Entry;
import be.kwakeroni.parameters.client.api.query.Query;
import be.kwakeroni.parameters.client.api.query.PartialQuery;
import be.kwakeroni.parameters.client.model.EntryType;
import be.kwakeroni.parameters.client.model.Parameter;
import be.kwakeroni.parameters.client.model.basic.Mapped;
import be.kwakeroni.parameters.client.model.basic.Ranged;
import be.kwakeroni.parameters.client.model.basic.Simple;

import java.util.function.Function;

/**
 * (C) 2016 Maarten Van Puymbroeck
 */
public class BasicQueries {

    private BasicQueries(){

    }

    public static <V, Missing extends EntryType> PartialQuery<Ranged<V, Missing>, Missing> entryAt(V value, Function<V, String> type){
        return new RangedQuery.Partial<>(value, type);
    }

    public static <K, Missing extends EntryType> PartialQuery<Mapped<K, Missing>, Missing> forKey(K key, Function<K, String> keyStringConverter){
        return new MappedQuery.Partial<>(key, keyStringConverter);
    }

    public static <P> Query<Simple, P> valueOf(Parameter<P> parameter){
        return new ValueQuery<>(parameter);
    }

    public static Query<Simple, Entry> entry(){
        return new EntryQuery();
    }
}
