package be.kwakeroni.evelyn.model.impl;

import be.kwakeroni.evelyn.model.DatabaseAccessor;
import be.kwakeroni.evelyn.model.Event;
import be.kwakeroni.evelyn.model.ParseException;
import be.kwakeroni.evelyn.storage.Storage;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

public final class DefaultDatabaseAccessor implements DatabaseAccessor {

    private static final String ATTRIBUTE_VERSION = "version";
    private static final String ATTRIBUTE_DB_NAME = "name";

    private final String version;
    private final String databaseName;
    private final Storage storage;
    private final Map<String, String> attributes;
    private final FileStructure fileStructure;

    public DefaultDatabaseAccessor(String version, String databaseName, Storage storage) {
        this(version, databaseName, storage, new HashMap<>(), FileStructure.getInstance());
    }

    DefaultDatabaseAccessor(String version, String databaseName, Storage storage, Map<String, String> attributes, FileStructure fileStructure) {
        this.version = Objects.requireNonNull(version, "version");
        this.databaseName = Objects.requireNonNull(databaseName, "databaseName");
        this.storage = Objects.requireNonNull(storage, "storage");
        this.fileStructure = Objects.requireNonNull(fileStructure, "fileStructure");
        this.attributes = new HashMap<>(Objects.requireNonNull(attributes, "attributes"));
        this.attributes.put(ATTRIBUTE_VERSION, this.version);
        this.attributes.put(ATTRIBUTE_DB_NAME, this.databaseName);
    }

    public static DefaultDatabaseAccessor createFrom(Storage storage) throws ParseException {
        return createFrom(storage, FileStructure.getInstance());
    }

    static DefaultDatabaseAccessor createFrom(Storage storage, FileStructure fileStructure) throws ParseException {
        Map<String, String> attributes = checkRequiredAttributes(fileStructure.readAttributes(storage));
        return new DefaultDatabaseAccessor(attributes.get(ATTRIBUTE_VERSION), attributes.get(ATTRIBUTE_DB_NAME), storage, attributes, fileStructure);
    }

    @Override
    public String getSpecVersion() {
        return this.version;
    }

    @Override
    public String getDatabaseName() {
        return this.databaseName;
    }

    @Override
    public void createDatabase() {
        this.fileStructure.initializeStorage(storage, attributes, ATTRIBUTE_VERSION);
    }

    public String getAttribute(String name) {
        return this.attributes.get(name);
    }

    @Override
    public Stream<Event> getData() throws ParseException {
        Stream<String> stream = fileStructure.readData(storage);
        try {
            return stream.map(this::toEvent);
        } catch (RuntimeException exc) {
            // Ensure the stream is closed in case of an Exception
            try (Stream<String> toClose = stream) {
                throw exc;
            }
        }
    }

    Event toEvent(String data) {
        return () -> data;
    }

    private static Map<String, String> checkRequiredAttributes(Map<String, String> attributes) throws ParseException {
        checkRequiredAttributes(attributes, ATTRIBUTE_VERSION, ATTRIBUTE_DB_NAME);
        return attributes;
    }

    private static void checkRequiredAttributes(Map<String, String> attributes, String... required) throws ParseException {
        for (String attribute : required) {
            if (!attributes.containsKey(attribute)) {
                throw new ParseException("Unspecified attribute: " + attribute);
            }
        }
    }

}
