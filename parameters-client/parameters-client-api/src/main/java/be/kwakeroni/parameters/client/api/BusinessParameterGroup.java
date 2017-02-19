package be.kwakeroni.parameters.client.api;

import be.kwakeroni.parameters.client.api.query.Query;
import be.kwakeroni.parameters.client.api.model.EntryType;

import java.util.Optional;

/**
 * Retrieves values of business parameters of a specific group.
 */
public interface BusinessParameterGroup<ET extends EntryType> {

    public String getName();

    public <T> Optional<T> get(Query<ET, T> query);

}
