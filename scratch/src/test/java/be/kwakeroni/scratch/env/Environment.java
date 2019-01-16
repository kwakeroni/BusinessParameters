package be.kwakeroni.scratch.env;

import be.kwakeroni.parameters.adapter.direct.factory.DirectBusinessParametersServiceFactory;
import be.kwakeroni.parameters.client.api.BusinessParameters;
import be.kwakeroni.parameters.client.api.WritableBusinessParameters;
import be.kwakeroni.parameters.client.api.factory.BusinessParametersFactory;
import be.kwakeroni.parameters.client.api.model.Entry;
import be.kwakeroni.parameters.client.api.model.EntryType;
import be.kwakeroni.parameters.client.api.model.ParameterGroup;
import be.kwakeroni.parameters.client.api.query.Query;
import be.kwakeroni.scratch.env.inmemory.TransientInMemoryTestData;
import org.junit.Assume;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;
import org.slf4j.Logger;

import java.util.Collections;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * (C) 2016 Maarten Van Puymbroeck
 */
public class Environment implements TestRule, AutoCloseable {

    private static Logger LOG = org.slf4j.LoggerFactory.getLogger(Environment.class);

    public static void main(String[] args) {
        new Environment();
    }

    @Override
    public Statement apply(Statement base, Description description) {
        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                try (Environment env = Environment.this) {
                    base.evaluate();
                }
            }
        };
    }

    private final TestData testData;
    protected WritableBusinessParameters parameters;

    public Environment() {
        this(TransientInMemoryTestData::new);
    }

    public Environment(Supplier<TestData> testDataSupplier) {
        this.testData = testDataSupplier.get();
        this.parameters = createBusinessParameters();
    }

    private WritableBusinessParameters createBusinessParameters() {
        BusinessParametersFactory factory = Services.loadService(BusinessParametersFactory.class);
        ((DirectBusinessParametersServiceFactory) factory).setBackendType(this.testData::acceptBackend);
        WritableBusinessParameters local = factory.getWritableInstance(Collections.emptyMap());
        return new WritableBusinessParameters() {
            @Override
            public <ET extends EntryType, T> void set(ParameterGroup<ET> group, Query<ET, T> query, T value) {
                local.set(group, query, value);
                testData.notifyModifiedGroup(group.getName());
            }

            @Override
            public void addEntry(ParameterGroup<?> group, Entry entry) {
                local.addEntry(group, entry);
                testData.notifyModifiedGroup(group.getName());
            }

            @Override
            public <ET extends EntryType, T> Optional<T> get(ParameterGroup<ET> group, Query<ET, T> query) {
                return local.get(group, query);
            }
        };
    }

    @Override
    public void close() throws Exception {
        this.testData.close();
    }

    public BusinessParameters getBusinessParameters() {
        return this.parameters;
    }

    public WritableBusinessParameters getWritableBusinessParameters() {
        return this.parameters;
    }

    public void runTestForGroup(ParameterGroup<?> group) {
        runTestForGroup(group.getName());
    }

    public void runTestForGroup(String groupName) {
        Assume.assumeTrue(this.testData.hasDataForGroup(groupName));
    }


    public TestRule reset() {
        return (base, description) -> new Statement() {
            @Override
            public void evaluate() throws Throwable {
                Environment.this.testData.reset();
                Environment.this.parameters = createBusinessParameters();
                base.evaluate();
            }
        };
    }

}
