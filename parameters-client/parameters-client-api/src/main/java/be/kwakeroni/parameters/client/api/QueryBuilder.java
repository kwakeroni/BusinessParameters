package be.kwakeroni.parameters.client.api;

import be.kwakeroni.parameters.client.model.EntryType;
import be.kwakeroni.parameters.client.model.Parameter;

@FunctionalInterface
public interface QueryBuilder<ET extends EntryType, T> {

    public Query<ET, T> build();

}
