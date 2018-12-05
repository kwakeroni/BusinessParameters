package be.kwakeroni.parameters.client.rest.factory;

import be.kwakeroni.parameters.client.api.BusinessParameters;
import be.kwakeroni.parameters.client.api.WritableBusinessParameters;
import be.kwakeroni.parameters.client.api.factory.BusinessParametersFactory;
import be.kwakeroni.parameters.client.api.factory.ClientWireFormatterFactory;
import be.kwakeroni.parameters.client.api.query.ClientWireFormatterContext;
import be.kwakeroni.parameters.client.rest.RestBusinessParametersClient;
import be.kwakeroni.parameters.core.support.client.DefaultClientWireFormatterContext;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static be.kwakeroni.parameters.core.support.service.BusinessParameterServices.loadServices;

public class RestBusinessParametersFactory implements BusinessParametersFactory {

    public static final Set<String> SUPPORTED_WIREFORMATS = Collections.unmodifiableSet(new HashSet<>(Arrays.asList("json")));

    @Override
    public BusinessParameters getInstance(Map<String, String> properties) {
        return getWritableInstance(properties);
    }

    @Override
    public WritableBusinessParameters getWritableInstance(Map<String, String> properties) {
        return createClient(getUrl(properties), createWireFormatterContext());
    }

    RestBusinessParametersClient createClient(String url, ClientWireFormatterContext context) {
        return new RestBusinessParametersClient(url, context);
    }

    private ClientWireFormatterContext createWireFormatterContext() {
        DefaultClientWireFormatterContext clientRegistry = new DefaultClientWireFormatterContext();
        loadServices(ClientWireFormatterFactory.class)
                .filter(factory -> SUPPORTED_WIREFORMATS.contains(factory.getWireFormat()))
                .forEach(clientRegistry::register);
        return clientRegistry;
    }

    private String getUrl(Map<String, String> properties) {
        String url = properties.get(Config.REST_URL);
        if (url == null) {
            throw new IllegalStateException("Missing configuration property: " + Config.REST_URL);
        }
        return url;
    }
}
