package be.kwakeroni.parameters.backend.es.service;

import java.util.Arrays;
import java.util.Properties;

/**
 * (C) 2017 Maarten Van Puymbroeck
 */
public class Configuration {

    private final Properties properties;

    public Configuration(Properties properties) {
        this.properties = requireComplete(properties);
    }

    private Properties requireComplete(Properties properties) {
        Arrays.stream(ConfigProperty.values())
                .map(ConfigProperty::getPropertyName)
                .filter(name -> ! properties.containsKey(name))
                .reduce((s1, s2) -> s1 + "," + s2)
                .ifPresent(missingProperties -> {
                    throw new IllegalStateException("ElasticSearch configuration incomplete: Missing properties: " + missingProperties);
                });

        return properties;
    }

    public String get(ConfigProperty property){
        return this.properties.getProperty(property.getPropertyName());
    }

    public String getServerUrl(){
        return get(ConfigProperty.SERVER_URL);
    }

    public String getIndexPath(){
        return get(ConfigProperty.INDEX_PATH);
    }
}
