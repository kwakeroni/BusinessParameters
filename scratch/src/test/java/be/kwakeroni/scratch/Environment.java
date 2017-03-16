package be.kwakeroni.scratch;

import be.kwakeroni.parameters.client.api.BusinessParameters;
import be.kwakeroni.parameters.client.api.WritableBusinessParameters;
import be.kwakeroni.parameters.client.api.factory.BusinessParametersFactory;
import be.kwakeroni.parameters.client.api.model.Entry;
import be.kwakeroni.parameters.client.api.model.EntryType;
import be.kwakeroni.parameters.client.api.model.ParameterGroup;
import be.kwakeroni.parameters.client.api.query.Query;
import org.junit.Assume;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;
import org.slf4j.Logger;

import java.util.Iterator;
import java.util.Optional;
import java.util.ServiceLoader;
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
    protected final WritableBusinessParameters parameters;

    public Environment() {
        this(InMemoryTestData::new);
    }

    public Environment(Supplier<TestData> testData) {
        this.testData = testData.get();
        BusinessParametersFactory factory = load(BusinessParametersFactory.class);
        WritableBusinessParameters local = factory.getWritableInstance();
        this.parameters = new WritableBusinessParameters() {
            @Override
            public <ET extends EntryType, T> void set(ParameterGroup<ET> group, Query<ET, T> query, T value) {
                local.set(group, query, value);
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException exc){

                }
            }

            @Override
            public void addEntry(ParameterGroup<?> group, Entry entry) {
                local.addEntry(group, entry);
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException exc){

                }
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
        Assume.assumeTrue(this.testData.hasDataForGroup(group.getName()));
    }

    private <S> S load(Class<S> serviceType) {
        ServiceLoader<S> loader = ServiceLoader.load(serviceType);
        Iterator<S> services = loader.iterator();
        if (!services.hasNext()) {
            throw new IllegalStateException("Service not found: " + serviceType.getName());
        }
        S service = services.next();
        if (services.hasNext()) {
            throw new IllegalStateException("Multiple services of type " + serviceType.getName() + ": " + service.getClass().getName() + " & " + services.next().getClass().getName());
        }
        return service;
    }

    public TestRule reset() {
        return (base, description) -> new Statement() {
            @Override
            public void evaluate() throws Throwable {
                Environment.this.testData.reset();
                base.evaluate();
            }
        };
    }


}
