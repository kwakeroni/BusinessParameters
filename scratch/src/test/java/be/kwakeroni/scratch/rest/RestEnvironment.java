package be.kwakeroni.scratch.rest;

import be.kwakeroni.parameters.adapter.rest.RestBackendAdapter;
import be.kwakeroni.parameters.adapter.rest.factory.RestBackendAdapterFactory;
import be.kwakeroni.scratch.InMemoryTestData;
import be.kwakeroni.scratch.TestData;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

import javax.swing.*;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Collections;
import java.util.function.Supplier;

/**
 * Created by kwakeroni on 10/10/17.
 */
public class RestEnvironment implements TestRule, AutoCloseable {

    public static void main(String[] args) throws Exception {
        try (RestEnvironment env = new RestEnvironment()) {
            JOptionPane.showMessageDialog(null, "Rest server running.\n\nClose window to stop server.", "Rest Server", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    @Override
    public Statement apply(Statement base, Description description) {
        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                try (RestEnvironment env = RestEnvironment.this) {
                    base.evaluate();
                }
            }
        };
    }

    @Override
    public void close() throws Exception {
        this.restServer.tearDown();
        this.testData.close();
    }

    private final TestData testData;
    private final RestServer restServer;

    public RestEnvironment() {
        this(InMemoryTestData::new);
    }

    public RestEnvironment(Supplier<TestData> testDataSupplier) {
        this.testData = testDataSupplier.get();
        RestBackendAdapterFactory factory = new RestBackendAdapterFactory();
        factory.setBackendType(this.testData::acceptBackend);
        RestBackendAdapter adapter = factory.newInstance();

        this.restServer = new RestServer("http://localhost:8080/parameters", Collections.singleton(adapter));
        try {
            this.restServer.setUp();
        } catch (IOException exc) {
            throw new UncheckedIOException(exc);
        }
    }

}
