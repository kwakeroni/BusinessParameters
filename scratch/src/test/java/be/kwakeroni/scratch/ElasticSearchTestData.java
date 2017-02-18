package be.kwakeroni.scratch;

import be.kwakeroni.parameters.backend.inmemory.api.EntryData;
import be.kwakeroni.scratch.tv.Dag;
import be.kwakeroni.scratch.tv.SimpleTVGroup;
import be.kwakeroni.scratch.tv.Slot;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import org.json.JSONObject;
import org.slf4j.Logger;

import java.util.UUID;

/**
 * (C) 2017 Maarten Van Puymbroeck
 */
public class ElasticSearchTestData implements TestData {
    private static Logger LOG = org.slf4j.LoggerFactory.getLogger(ElasticSearchTestData.class);

    private final ElasticSearchTestNode elasticSearch;
    private final Client client = new Client();

    public ElasticSearchTestData() {
        this.elasticSearch = ElasticSearchTestNode.start();
        this.elasticSearch.waitUntilStarted();
        reset();
    }

    @Override
    public void close() throws Exception {
        this.elasticSearch.close();
    }

    @Override
    public void reset() {
        callES("/parameters", WebResource::delete);
        insert(SimpleTVGroup.instance().getName());
    }


    private void insert(String group){
        String url = String.format("/parameters/%s/%s",
                group,
                UUID.randomUUID().toString()

        );
        callES(url, toJson(SimpleTVGroup.getEntryData(Dag.MAANDAG, Slot.atHour(20))), WebResource::put);
    }

    private void callES(String path, String body, CallWithBody call){
        String url = resolve("http://127.0.0.1:9200", path);
        LOG.info("{} : {}\r\n{}", url, call, body);
        ClientResponse response = call.call(client.resource(url), body);
        LOG.info("[{}] {}\r\n{}", response.getStatus(), url, response.getEntity(String.class));
    }

    private void callES(String path, CallWithoutBody call){
        String url = resolve("http://127.0.0.1:9200", path);
        LOG.info("{} : {}", url, call);
        ClientResponse response = call.call(client.resource(url));
        LOG.info("[{}] {}\r\n{}", response.getStatus(), url, response.getEntity(String.class));
    }

    private String resolve(String base, String path){
        return base + ((path.startsWith("/"))? "" : "/") + path + "?pretty";
    }

    private String toJson(EntryData entry){
        return new JSONObject(entry.asMap()).toString(4);
    }

    private static interface CallWithoutBody {
        <T> T call(WebResource resource, Class<T> type);

        default ClientResponse call(WebResource resource){
            return call(resource, ClientResponse.class);
        }
    }

    private static interface CallWithBody {
        <T> T call(WebResource resource, Class<T> type, String body);

        default ClientResponse call(WebResource resource, String body){
            return call(resource, ClientResponse.class, body);
        }
    }

}
