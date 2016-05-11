package be.kwakeroni.parameters.client.model.basic;

import be.kwakeroni.parameters.client.api.Entry;
import be.kwakeroni.parameters.client.api.Query;
import be.kwakeroni.parameters.client.model.EntryType;
import be.kwakeroni.parameters.client.model.Parameter;
import be.kwakeroni.parameters.client.query.basic.EntryQuery;
import be.kwakeroni.parameters.client.query.basic.ValueQuery;

/**
 * Created by kwakeroni on 6/05/2016.
 */
public interface Simple extends EntryType {
    public <T> T value(Parameter<T> parameter);

    public Entry entry();

    public <T> T get(Query<Simple, T> query);
}
