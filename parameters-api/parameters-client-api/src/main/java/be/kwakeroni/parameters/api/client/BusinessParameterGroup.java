package be.kwakeroni.parameters.api.client;

import be.kwakeroni.parameters.api.client.query.Query;
import be.kwakeroni.parameters.api.client.model.EntryType;

/**
 * Retrieves values of business parameters of a specific group.
 */
public interface BusinessParameterGroup<ET extends EntryType> {

    public String getName();

    public <T> T get(Query<ET, T> query);

}
