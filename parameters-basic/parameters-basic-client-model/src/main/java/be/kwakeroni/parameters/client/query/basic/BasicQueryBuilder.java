package be.kwakeroni.parameters.client.query.basic;


import be.kwakeroni.parameters.client.api.Entry;
import be.kwakeroni.parameters.client.api.QueryBuilder;
import be.kwakeroni.parameters.client.model.EntryType;
import be.kwakeroni.parameters.client.model.Parameter;
import be.kwakeroni.parameters.client.model.basic.Mapped;
import be.kwakeroni.parameters.client.model.basic.Ranged;
import be.kwakeroni.parameters.client.model.basic.Simple;

public interface BasicQueryBuilder<ET extends EntryType, T> extends QueryBuilder<ET, T> {

    public static <T> BasicQueryBuilder<Simple, T> valueOf(Parameter<T> parameter){
        return () -> new ValueQuery<>(parameter);
    }

    public static BasicQueryBuilder<Simple, Entry> entry(){
        return () -> new EntryQuery();
    }

    public default <V> BasicQueryBuilder<Ranged<V, ET>, T> at(V value) {
        return () -> new RangedQuery<>(value, this.build());
    }

    public default BasicQueryBuilder<Mapped<ET>, T> at(Entry key) {
        return null;
    }
}
