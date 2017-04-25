package be.kwakeroni.scratch;

import be.kwakeroni.parameters.backend.api.factory.BusinessParametersBackendFactory;
import be.kwakeroni.parameters.backend.es.api.ElasticSearchGroup;
import be.kwakeroni.parameters.backend.es.factory.ElasticSearchBackendServiceFactory;
import be.kwakeroni.parameters.backend.es.service.ElasticSearchBackend;
import be.kwakeroni.parameters.backend.inmemory.api.EntryData;
import be.kwakeroni.scratch.tv.*;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import org.json.JSONObject;
import org.slf4j.Logger;

import java.io.ByteArrayInputStream;
import java.util.*;

/**
 * (C) 2017 Maarten Van Puymbroeck
 */
public class ElasticSearchTestData implements TestData {
    private static Logger LOG = org.slf4j.LoggerFactory.getLogger(ElasticSearchTestData.class);

    private final ElasticSearchTestNode elasticSearch;
    private final ElasticSearchBackend backend;
    private final Client client = new Client();
    private final List<String> groups = new ArrayList<>();


    public ElasticSearchTestData() {
        this.backend = ElasticSearchBackendServiceFactory.getSingletonInstance();
        this.elasticSearch = ElasticSearchTestNode.getRunningInstance();
        reset();
    }

    @Override
    public void close() throws Exception {

    }

    @Override
    public void reset() {
        try {
            callES("/parameters", WebResource::put);
        } catch (Exception exc) {
        }

        this.backend.getGroupNames().forEach(this.backend::unregisterGroup);
        this.groups.clear();
        try {
            callES("/parameters", WebResource::delete);
        } catch (Exception exc) {
        }

        LOG.info("Inserting test data...");

        Map<String, List<String>> uuids = new HashMap<>();

        register(SimpleTVGroup.ELASTICSEARCH_GROUP);
        addInsert(uuids, SimpleTVGroup.instance().getName(), SimpleTVGroup.getEntryData(Dag.MAANDAG, Slot.atHour(20)));

        register(MappedTVGroup.ELASTICSEARCH_GROUP);
        addInsert(uuids, MappedTVGroup.instance().getName(), MappedTVGroup.entryData(Dag.ZATERDAG, "Samson"));
        addInsert(uuids, MappedTVGroup.instance().getName(), MappedTVGroup.entryData(Dag.ZONDAG, "Morgen Maandag"));

        boolean addRangeLimits = true;

        register(RangedTVGroup.elasticSearchGroup(addRangeLimits));
        addInsert(uuids, RangedTVGroup.instance().getName(),
                RangedTVGroup.entryData(Slot.atHour(8), Slot.atHour(12), "Samson", addRangeLimits),
                RangedTVGroup.entryData(Slot.atHalfPast(20), Slot.atHour(22), "Morgen Maandag", addRangeLimits));

        register(MappedRangedTVGroup.elasticSearchGroup(addRangeLimits));
        addInsert(uuids, MappedRangedTVGroup.instance().getName(),
                MappedRangedTVGroup.entryData(Dag.MAANDAG, Slot.atHalfPast(20), Slot.atHour(22), "Gisteren Zondag", addRangeLimits),
                MappedRangedTVGroup.entryData(Dag.ZATERDAG, Slot.atHour(8), Slot.atHour(12), "Samson", addRangeLimits),
                MappedRangedTVGroup.entryData(Dag.ZATERDAG, Slot.atHour(14), Slot.atHour(18), "Koers", addRangeLimits),
                MappedRangedTVGroup.entryData(Dag.ZONDAG, Slot.atHalfPast(20), Slot.atHour(22), "Morgen Maandag", addRangeLimits)
        );

        LOG.info("Waiting for test data to become available...");

        try {
            this.elasticSearch.waitUntil(() -> this.isEmpty(uuids), 5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Object o = findAll(MappedRangedTVGroup.instance().getName());
        System.out.println(o);

    }

    private boolean isEmpty(Map<String, List<String>> uuids) {
        return uuids.keySet()
                .stream()
                .filter(group -> !isEmpty(group, uuids))
                .findAny()
                .map(s -> Boolean.FALSE)
                .orElse(Boolean.TRUE);
    }

    private boolean isEmpty(String group, Map<String, List<String>> uuids) {
        return isEmpty(group, uuids.getOrDefault(group, Collections.emptyList()));
    }

    private boolean isEmpty(String group, List<String> uuids) {

        Iterator<String> iter = uuids.iterator();
        while (iter.hasNext()) {
            Object o = get(group, iter.next());
            if (o != null) {
                iter.remove();
            }
        }

        return uuids.isEmpty();
    }

    @Override
    public void notifyModifiedGroup(String name) {
        try {
            Thread.sleep(5000);
        } catch (InterruptedException exc) {

        }
    }

    @Override
    public boolean acceptBackend(BusinessParametersBackendFactory factory) {
        return factory instanceof ElasticSearchBackendServiceFactory;
    }

    @Override
    public boolean hasDataForGroup(String name) {
        return groups.contains(name);
    }

    private void register(ElasticSearchGroup group) {
        backend.registerGroup(group);
        this.groups.add(group.getName());
    }

    private String get(String group, String uuid) {
        String url = String.format("/parameters/%s/%s", group, uuid);
        ClientResponse response = null;
        try {
            response = callES(url, WebResource::get);
        } catch (ResponseException e) {
            response = e.getResponse();
        }
        int status = response.getStatus();
        if (status == 204 || status == 404) {
            return null;
        } else if (status < 400) {
            return response.getEntity(String.class);
        } else {
            throw new RuntimeException(status + " - " + response.getEntity(String.class));
        }

    }

    private String findAll(String group) {
        String url = "/parameters/_search";
        String body = "{\"size\":20,\"query\":{\"bool\":{\"must\":[{\"match\":{\"_type\":\"" + group + "\"}}]}},\"from\":0}";
        ClientResponse response = callES(url, body, WebResource::post);
        int status = response.getStatus();
        if (status == 204 || status == 404) {
            return null;
        } else if (status < 400) {
            return response.getEntity(String.class);
        } else {
            throw new RuntimeException(status + " - " + response.getEntity(String.class));
        }

    }

    private void addInsert(Map<String, List<String>> accu, String group, EntryData... entryDatas) {
        accu.computeIfAbsent(group, key -> new ArrayList<>()).addAll(insert(group, entryDatas));
    }

    private List<String> insert(String group, EntryData... entryDatas) {
        List<String> uuids = new ArrayList<>();
        for (EntryData entryData : entryDatas) {
            uuids.add(insert(group, entryData.asMap()));
        }
        return uuids;
    }

    private void addInsert(Map<String, List<String>> accu, String group, Map<String, ?>... entryDatas) {
        accu.computeIfAbsent(group, key -> new ArrayList<>()).addAll(insert(group, entryDatas));
    }

    private List<String> insert(String group, Map<String, ?>... entryDatas) {
        List<String> uuids = new ArrayList<>();
        for (Map<String, ?> entryData : entryDatas) {
            uuids.add(insert(group, entryData));
        }
        return uuids;
    }

    private String insert(String group, Map<String, ?> entryData) {
        //String uuid = UUID.randomUUID().toString();
        String url = String.format("/parameters/%s", group);
        ClientResponse response = callES(url, toJson(entryData), WebResource::post);

        String uuid = new JSONObject(response.getEntity(String.class)).getString("_id");
        return uuid;
    }

    private ClientResponse callES(String path, String body, CallWithBody call) {
        String url = resolve("http://127.0.0.1:9200", path);
        LOG.info("{} : {}\r\n{}", url, call, body);
        ClientResponse response = call.call(client.resource(url), body);
        byte[] bytes = response.getEntity(byte[].class);
        response.setEntityInputStream(new ByteArrayInputStream(bytes));
        LOG.info("[{}] {}\r\n{}", response.getStatus(), url, new String(bytes));
        if (response.getStatus() >= 400) {
            throw new RuntimeException(String.format("Error calling ES: [%s] %s", response.getStatus(), new String(bytes)));
        }
        return response;
    }

    private ClientResponse callES(String path, CallWithoutBody call) throws ResponseException {
        String url = resolve("http://127.0.0.1:9200", path);
        LOG.info("{} : {}", url, call);
        ClientResponse response = call.call(client.resource(url));
        byte[] bytes = response.getEntity(byte[].class);
        response.setEntityInputStream(new ByteArrayInputStream(bytes));
        LOG.info("[{}] {}\r\n{}", response.getStatus(), url, new String(bytes));
        if (response.getStatus() >= 400) {
            throw new ResponseException(response, bytes);
        }
        return response;
    }

    private String resolve(String base, String path) {
        return base + ((path.startsWith("/")) ? "" : "/") + path + "?pretty";
    }

    private String toJson(EntryData entry) {
        return toJson(entry.asMap());
    }

    private String toJson(Map<String, ?> entry) {
        return new JSONObject(entry).toString(4);
    }

    private ClientResponse get(String url) {
        return client.resource(resolve("http://127.0.0.1:9200", url)).get(ClientResponse.class);
    }

    private static interface CallWithoutBody {
        <T> T call(WebResource resource, Class<T> type);

        default ClientResponse call(WebResource resource) {
            return call(resource, ClientResponse.class);
        }
    }

    private static interface CallWithBody {
        <T> T call(WebResource resource, Class<T> type, String body);

        default ClientResponse call(WebResource resource, String body) {
            return call(resource, ClientResponse.class, body);
        }
    }

    private static class ResponseException extends Exception {
        private final ClientResponse response;

        public ResponseException(ClientResponse response, byte[] bytes) {
            super(String.format("Error calling ES: [%s] %s", response.getStatus(), new String(bytes)));
            this.response = response;
        }

        public ClientResponse getResponse() {
            return response;
        }
    }
}
// INFO  - http://127.0.0.1:9200/13591bee-a2f9-431c-ab27-3572bf5aeb4f?pretty
//  http://127.0.0.1:9200/parameters/tv.simple/13591bee-a2f9-431c-ab27-3572bf5aeb4f?pretty