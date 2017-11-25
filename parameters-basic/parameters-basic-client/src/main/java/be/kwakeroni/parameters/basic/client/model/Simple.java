package be.kwakeroni.parameters.basic.client.model;

import be.kwakeroni.parameters.client.api.model.Entry;
import be.kwakeroni.parameters.client.api.model.EntryType;
import be.kwakeroni.parameters.client.api.model.Parameter;
import be.kwakeroni.parameters.client.api.query.Query;

import java.util.Optional;

/**
 * Created by kwakeroni on 6/05/2016.
 */
public interface Simple extends EntryType {
    public default <T> Optional<T> getValue(Parameter<T> parameter) {
        return get(Queries.valueOf(parameter));
    }

    public default Optional<Entry> getEntry() {
        return get(Queries.entry());
    }

    public <T> Optional<T> get(Query<Simple, T> query);
}
