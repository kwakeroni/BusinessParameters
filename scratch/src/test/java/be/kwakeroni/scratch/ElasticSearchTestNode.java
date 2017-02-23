package be.kwakeroni.scratch;

import org.elasticsearch.cli.Terminal;
import org.elasticsearch.common.network.NetworkModule;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.node.Node;
import org.elasticsearch.node.internal.InternalSettingsPreparer;
import org.elasticsearch.transport.Netty4Plugin;

import java.util.Arrays;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

/**
 * (C) 2017 Maarten Van Puymbroeck
 */
public class ElasticSearchTestNode implements AutoCloseable {

    private Thread thread;
    private AtomicBoolean started = new AtomicBoolean(false);

    private ElasticSearchTestNode(){
        thread = new Thread(this::run, "ElasticSearchTestNode Container Thread");
    }

    public static ElasticSearchTestNode start(){
        ElasticSearchTestNode node = new ElasticSearchTestNode();
        node.thread.start();
        return node;
    }

    public void waitUntilStarted() {
        try {
            waitUntil(started::get);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void stopWhen(BooleanSupplier condition) throws InterruptedException {
        stopWhen((Supplier<Boolean>) condition::getAsBoolean);
    }

    public void stopWhen(Supplier<Boolean> condition) throws InterruptedException {
        try {
            waitUntil(condition);
        } finally {
            stop();
        }
    }

    public void waitUntil(Supplier<Boolean> condition) throws InterruptedException {
        waitUntil(condition, 1000);
    }
        public void waitUntil(Supplier<Boolean> condition, int delay) throws InterruptedException {
        while(! Boolean.TRUE.equals(condition.get())){
            Thread.sleep(delay);
        }
    }

    public void stop() throws InterruptedException {
        if (thread != null) {
        thread.interrupt();
        System.out.println("Interrupting thread");
        thread.join();
        System.out.println("Thread joined");
            thread = null;
        }
    }

    @Override
    public void close() throws Exception {
            stop();
    }

    private void run(){
        try (Node node = startNode()) {
            while (!Thread.interrupted()) {
                Thread.sleep(1000);
            }
            System.out.println("Thread interrupted()");
        } catch (InterruptedException exc){
            System.out.println("Thread interrupted");
        } catch (RuntimeException exc){
            throw exc;
        } catch (Exception exc){
            throw new RuntimeException(exc);
        } finally {
            ElasticSearchTestNode.this.started.set(false);
        }
    }

    private Node startNode() throws Exception {
        Settings settings = Settings.builder()
//                .put("path.home", "c:\\Projects\\elasticsearch-5.2.1")
                .put("path.home", "./.es")
                .put(NetworkModule.TRANSPORT_TYPE_KEY, NetworkModule.LOCAL_TRANSPORT)
                .put(NetworkModule.HTTP_TYPE_KEY, "netty4")
                .build();

        Node node = new Node(
                InternalSettingsPreparer.prepareEnvironment(settings, (Terminal)null),
                Arrays.asList(Netty4Plugin.class)){};
        System.out.println("Starting node");
        node.start();
        System.out.println("Node running");
        ElasticSearchTestNode.this.started.set(true);

        return node;
    }


}
