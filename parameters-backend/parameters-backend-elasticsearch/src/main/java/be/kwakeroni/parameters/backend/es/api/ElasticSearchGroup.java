package be.kwakeroni.parameters.backend.es.api;

import be.kwakeroni.parameters.backend.api.BackendGroup;

/**
 * (C) 2017 Maarten Van Puymbroeck
 */
public interface ElasticSearchGroup extends BackendGroup<ElasticSearchQuery<?>> {

    public ElasticSearchEntry prepareAndValidateNewEntry(ElasticSearchEntry entry, ElasticSearchData storage);

}
