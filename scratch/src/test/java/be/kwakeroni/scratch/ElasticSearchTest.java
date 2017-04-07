package be.kwakeroni.scratch;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import org.junit.ClassRule;
import org.junit.Ignore;
import org.junit.Test;

import javax.swing.*;
import java.util.Arrays;
import java.util.function.Function;

/**
 * (C) 2017 Maarten Van Puymbroeck
 */
@Ignore
public class ElasticSearchTest {

    @ClassRule
    public static Environment environment = new Environment(ElasticSearchTestData::new);

    @Test
    public void test() throws Exception {
        System.out.println("Started test");

        dump("/_cat/indices?v");

        JOptionPane.showConfirmDialog(null, "Click OK to stop server", "ElasticSearch server runnning...", JOptionPane.OK_OPTION);
    }

    private String resolve(String base, String path) {
        return base + ((path.startsWith("/")) ? "" : "/") + path;
    }

    public WebResource call(String path) {
        return new Client().resource(resolve("http://127.0.0.1:9200", path));
    }

    // http://127.0.0.1:9200/_cat/indices?v
    private String dump(String path) {
        return dump(call(path).get(ClientResponse.class));
    }

    private String dump(ClientResponse response) {
        String str = format(response,
                "[%s] %s",
                ClientResponse::getStatus,
                r -> r.getEntity(String.class)
        );
        System.out.println(str);
        return str;
    }

    @SafeVarargs
    private final <T> String format(T t, String pattern, Function<T, ?>... args) {
        return String.format(pattern,
                Arrays.stream(args).map(func -> func.apply(t)).toArray()
        );
    }

//    c:\Projects\elasticsearch-5.2.1\bin>elasticsearch.bat
//    "C:\Program Files\Java\jdk1.8.0_25\bin\java.exe"  -Xms2g -Xmx2g -XX:+UseConcMarkSweepGC -XX:CMSInitiatingOccupancyFracti
//            on=75 -XX:+UseCMSInitiatingOccupancyOnly -XX:+DisableExplicitGC -XX:+AlwaysPreTouch -server -Xss1m -Djava.awt.headless=t
//    rue -Dfile.encoding=UTF-8 -Djna.nosys=true -Djdk.io.permissionsUseCanonicalPath=true -Dio.netty.noUnsafe=true -Dio.netty
//            .noKeySetOptimization=true -Dio.netty.recycler.maxCapacityPerThread=0 -Dlog4j.shutdownHookEnabled=false -Dlog4j2.disable
//            .jmx=true -Dlog4j.skipJansi=true -XX:+HeapDumpOnOutOfMemoryError  -Delasticsearch -Des.path.home="c:\Projects\elasticsea
//    rch-5.2.1" -cp "c:\Projects\elasticsearch-5.2.1/lib/elasticsearch-5.2.1.jar;c:\Projects\elasticsearch-5.2.1/lib/*" "org.
//elasticsearch.bootstrap.Elasticsearch"
}
