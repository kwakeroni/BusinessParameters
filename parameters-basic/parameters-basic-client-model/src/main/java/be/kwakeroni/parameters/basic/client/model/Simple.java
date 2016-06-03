package be.kwakeroni.parameters.basic.client.model;

import be.kwakeroni.parameters.basic.client.query.BasicQueries;
import be.kwakeroni.parameters.client.api.entry.Entry;
import be.kwakeroni.parameters.client.api.query.Query;
import be.kwakeroni.parameters.client.model.EntryType;
import be.kwakeroni.parameters.client.model.Parameter;

/**
 * Created by kwakeroni on 6/05/2016.
 */
public interface Simple extends EntryType {
    public default <T> T getValue(Parameter<T> parameter){
        return get(BasicQueries.valueOf(parameter));
    }

    public default Entry getEntry() {
        return get(BasicQueries.entry());
    }

    public <T> T get(Query<Simple, T> query);
}
