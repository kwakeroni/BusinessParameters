package be.kwakeroni.parameters.backend.es.api;

import org.json.JSONObject;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * (C) 2017 Maarten Van Puymbroeck
 */
public interface ElasticSearchData {

    public default ElasticSearchData with(Consumer<ElasticSearchCriteria> implicitCriteria){
        return (criteria, pageSize) ->
                findAll(implicitCriteria.andThen(criteria), pageSize);
    }

    public default ElasticSearchData withFilter(Function<Stream<ElasticSearchEntry>, Stream<ElasticSearchEntry>> filter){
        return (criteria, pageSize) ->
                filter.apply(ElasticSearchData.this.findAll(criteria, pageSize));
    }

    public default Stream<ElasticSearchEntry> findAll(int pageSize){
        return findAll(criteria -> {}, pageSize);
    }

    public Stream<ElasticSearchEntry> findAll(Consumer<ElasticSearchCriteria> criteria, int pageSize);

}
