package be.kwakeroni.parameters.basic.definition.jmx.factory;

import be.kwakeroni.parameters.backend.api.factory.BackendWireFormatterFactory;
import be.kwakeroni.parameters.basic.backend.query.BasicBackendWireFormatter;
import be.kwakeroni.parameters.basic.definition.jmx.BasicJMXBackendWireFormatter;

import java.util.function.Consumer;

/**
 * Created by kwakeroni on 15/05/17.
 */
public class BasicJMXBackendWireFormatterFactory implements BackendWireFormatterFactory {

    @Override
    public String getWireFormat() {
        return "jmx";
    }

    @Override
    public void registerInstance(Registry registry) {
        registry.register(BasicBackendWireFormatter.class, new BasicJMXBackendWireFormatter());
    }

    @Override
    public void unregisterInstance(Consumer<Class<?>> registry) {
        registry.accept(BasicBackendWireFormatter.class);
    }
}
