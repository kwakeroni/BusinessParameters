package be.kwakeroni.parameters.basic.client.model;

import be.kwakeroni.parameters.basic.client.query.EntryQuery;
import be.kwakeroni.parameters.basic.client.query.ValueQuery;
import be.kwakeroni.parameters.client.api.model.Entry;
import be.kwakeroni.parameters.client.api.model.Parameter;
import be.kwakeroni.parameters.client.api.query.Query;

/**
 * Created by kwakeroni on 10/11/17.
 */
abstract class Queries {

    static <T> Query<Simple, T> valueOf(Parameter<T> parameter) {
        return new ValueQuery<T>(parameter);
    }

    private static final Query<Simple, Entry> ENTRY_QUERY = new EntryQuery();

    static Query<Simple, Entry> entry() {
        return ENTRY_QUERY;
    }
}
