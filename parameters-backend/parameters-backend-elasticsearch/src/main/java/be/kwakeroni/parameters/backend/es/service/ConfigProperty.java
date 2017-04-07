package be.kwakeroni.parameters.backend.es.service;

/**
 * (C) 2017 Maarten Van Puymbroeck
 */
public enum ConfigProperty {
    SERVER_URL("server.url"),
    INDEX_PATH("server.indexPath");

    private final String name;

    private ConfigProperty(String name) {
        this.name = name;
    }

    public String getPropertyName(){
        return this.name;
    }


}
