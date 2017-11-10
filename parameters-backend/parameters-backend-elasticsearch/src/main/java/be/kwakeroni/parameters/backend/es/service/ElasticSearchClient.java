package be.kwakeroni.parameters.backend.es.service;

import be.kwakeroni.parameters.backend.es.api.ElasticSearchEntry;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.Response;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;

/**
 * (C) 2017 Maarten Van Puymbroeck
 */
class ElasticSearchClient {

    private static Logger LOG = LoggerFactory.getLogger(ElasticSearchClient.class);

    private Client client;
    private WebResource index;
    private WebResource search;

    ElasticSearchClient(Configuration configuration) {
        this.client = new Client();

        String indexPath = normalizeRelativePath(configuration.getIndexPath());
        String url = configuration.getServerUrl() + indexPath;

        this.index = this.client.resource(url);
        this.search = this.client.resource(url + "/_search");
    }

    JSONObject getById(String group, String id) {
        ClientResponse response = index.path(group + "/" + id).get(ClientResponse.class);
        String entity = extractEntity(response, String.class);
        return new JSONObject(entity);
    }

    Stream<JSONObject> getAggregation(String name, JSONObject terms) {
        JSONObject request = new JSONObject();
        request.put("size", 0);
        request.put("aggs",
                new JSONObject().put(name,
                        new JSONObject().put("terms",
                                terms)));
//        request.put("from", page * pageSize);

        ClientResponse response = search.post(ClientResponse.class, request.toString());
        String entity = extractEntity(response, String.class);
        JSONObject result = new JSONObject(entity);
        JSONArray array = result.getJSONObject("aggregations").getJSONObject(name).getJSONArray("buckets");
        return IntStream.range(0, array.length())
                .mapToObj(array::getJSONObject);

    }

    Stream<ElasticSearchEntry> query(JSONObject query, int pageSize) {
        JSONObject first = query(query, 0, pageSize);
        long totalHits = first.getJSONObject("hits").getLong("total");
        // 0->0, 1->1, 10->1, 11->2, 19->, 20->2, 21->3

        if (totalHits == 0) {
            return Stream.empty();
        } else {
            long pages = 1 + (totalHits - 1) / pageSize;
            if (pages == 1) {
                return hits(first).map(DefaultElasticSearchEntry::new);
            } else {
                return Stream.concat(
                        hits(first),
                        LongStream
                                .range(1, pages)
                                .mapToObj(i -> query(query, i, pageSize))
                                .flatMap(this::hits)
                ).map(DefaultElasticSearchEntry::new);
            }
        }

    }

    Stream<JSONObject> hits(JSONObject searchResults) {
        JSONArray hits = searchResults.getJSONObject("hits").getJSONArray("hits");
        return IntStream.range(0, hits.length())
                .mapToObj(hits::getJSONObject);
    }

    JSONObject query(JSONObject query, long page, int pageSize) {
        JSONObject request = new JSONObject();
        request.put("query", query);
        request.put("size", pageSize);
        request.put("from", page * pageSize);

        String requestString = request.toString();
        LOG.debug("Sending ES query: {}", requestString);
        ClientResponse response = search.post(ClientResponse.class, requestString);
        String entity = extractEntity(response, String.class);

        JSONObject result = new JSONObject(entity);

        LOG.debug("ES result: {}", result.toString(2));

        return result;
    }

    JSONObject _search(String query) {
        ClientResponse response = search.post(ClientResponse.class, query);
        String entity = extractEntity(response, String.class);
        return new JSONObject(entity);
    }

    void insert(String group, ElasticSearchEntry entry){
        JSONObject data = new JSONObject(entry.toRawMap());
        insert(group, data);
    }
    private void insert(String group, JSONObject data) {
        String dataString = data.toString();
        LOG.debug("Sending ES insert to {}: {}", group, dataString);
        ClientResponse response = index.path(group).post(ClientResponse.class, dataString);
        Object o = extractEntity(response, String.class);
    }

    void update(String group, ElasticSearchEntry entry){
        String id = entry.getId();
        JSONObject data = new JSONObject(entry.toRawMap());
        update(group, id, data);
    }

    private void update(String group, String id, JSONObject data) {
        String dataString = data.toString();
        LOG.debug("Sending ES update to {}: {}", group+"/"+id, dataString);
        ClientResponse response = index.path(group+"/"+id).put(ClientResponse.class, dataString);
        Object o = extractEntity(response, String.class);
    }


    // Normalize to form /path
    private String normalizeRelativePath(String path) {
        if (path.endsWith("/")) {
            path = path.substring(0, path.length() - 1);
        }
        if (!path.startsWith("/")) {
            path = "/" + path;
        }
        return path;
    }

    private <T> T extractEntity(ClientResponse response, Class<T> type) {
        if (isError(response)) {
            throw toException(response);
        }

        return response.getEntity(type);
    }

    private boolean isError(ClientResponse response) {
        Response.Status.Family family = response.getStatusInfo().getFamily();
        return family == Response.Status.Family.CLIENT_ERROR
                || family == Response.Status.Family.SERVER_ERROR;
    }

    private RuntimeException toException(ClientResponse response) {
        throw new RuntimeException(
                String.format("[%s] %s: %s",
                        response.getStatus(),
                        response.getStatusInfo().getReasonPhrase(),
                        response.getEntity(String.class)
                ));
    }

}
