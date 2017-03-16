package be.kwakeroni.parameters.backend.es.factory;

import be.kwakeroni.parameters.backend.api.BusinessParametersBackend;
import be.kwakeroni.parameters.backend.api.factory.BusinessParametersBackendFactory;
import be.kwakeroni.parameters.backend.es.api.ElasticSearchQuery;
import be.kwakeroni.parameters.backend.es.service.Configuration;
import be.kwakeroni.parameters.backend.es.service.ElasticSearchBackend;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.Properties;

/**
 * (C) 2017 Maarten Van Puymbroeck
 */
public class ElasticSearchBackendServiceFactory implements BusinessParametersBackendFactory {

    public static final String CONFIG_FILE = "/parameters-backend-elasticsearch.properties";

    @Override
    public BusinessParametersBackend<ElasticSearchQuery<?>, ?, ?> getInstance() {
        return INSTANCE;
        // @todo remove singleton
//        return new ElasticSearchBackend(loadConfig());
    }

    private static Configuration loadConfig(){
        InputStream config = Configuration.class.getResourceAsStream(CONFIG_FILE);

        if (config == null){
            throw new IllegalStateException("ElasticSearch configuration incomplete: Config file not found: " + CONFIG_FILE);
        }

        Properties properties = new Properties();
        try {
            properties.load(config);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }

        return new Configuration(properties);
    }

    private static final ElasticSearchBackend INSTANCE = new ElasticSearchBackend(loadConfig());

    public static ElasticSearchBackend getSingletonInstance() {
        return INSTANCE;
    }
}
