package be.kwakeroni.evelyn.model.impl;

import be.kwakeroni.evelyn.model.DatabaseAccessor;
import be.kwakeroni.evelyn.model.Event;
import be.kwakeroni.evelyn.model.ParseException;
import be.kwakeroni.evelyn.model.RuntimeParseException;
import be.kwakeroni.evelyn.storage.Storage;
import be.kwakeroni.evelyn.storage.StorageExistsException;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

public final class DefaultDatabaseAccessor implements DatabaseAccessor {

    private static final String ATTRIBUTE_VERSION = "version";
    private static final String ATTRIBUTE_DB_NAME = "name";
    private static final String ATTRIBUTE_CHARSET = "charset";

    private final String version;
    private final String databaseName;
    private final Storage storage;
    private final Map<String, String> attributes;
    private final FileStructure fileStructure;
    private final RecordStructure recordStructure;
    private final Charset charset;

    public DefaultDatabaseAccessor(String version, String databaseName, Storage storage) {
        this(version, databaseName, storage, new HashMap<>(), FileStructure.getInstance(), RecordStructure.getInstance());
    }

    DefaultDatabaseAccessor(String version, String databaseName, Storage storage, Map<String, String> attributes, FileStructure fileStructure, RecordStructure recordStructure) {
        this.version = Objects.requireNonNull(version, "version");
        this.databaseName = Objects.requireNonNull(databaseName, "databaseName");
        this.storage = Objects.requireNonNull(storage, "storage");
        this.fileStructure = Objects.requireNonNull(fileStructure, "fileStructure");
        this.recordStructure = Objects.requireNonNull(recordStructure, "recordStructure");
        this.attributes = new HashMap<>(Objects.requireNonNull(attributes, "attributes"));
        this.attributes.put(ATTRIBUTE_VERSION, this.version);
        this.attributes.put(ATTRIBUTE_DB_NAME, this.databaseName);
        if (this.attributes.containsKey(ATTRIBUTE_CHARSET)) {
            this.charset = Charset.forName(this.attributes.get(ATTRIBUTE_CHARSET));
        } else {
            this.charset = Charset.defaultCharset();
            this.attributes.put(ATTRIBUTE_CHARSET, this.charset.name());
        }
    }

    public static DefaultDatabaseAccessor createFrom(Storage storage) throws ParseException {
        return createFrom(storage, FileStructure.getInstance());
    }

    static DefaultDatabaseAccessor createFrom(Storage storage, FileStructure fileStructure) throws ParseException {
        Map<String, String> attributes = checkRequiredAttributes(fileStructure.readAttributes(storage));
        return new DefaultDatabaseAccessor(attributes.get(ATTRIBUTE_VERSION), attributes.get(ATTRIBUTE_DB_NAME), storage, attributes, fileStructure, RecordStructure.getInstance());
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
    public void createDatabase() throws StorageExistsException {
        this.fileStructure.initializeStorage(storage, attributes, ATTRIBUTE_VERSION);
    }

    public String getAttribute(String name) {
        return this.attributes.get(name);
    }

    @Override
    public void append(Event event) {
        this.storage.append(recordStructure.toData(event));
    }

    @Override
    public Stream<Event> getData() throws ParseException {
        FileStructure.StreamData streamData = fileStructure.readData(storage, charset);
        try {
            // Effects of line counter with parallel streams to be investigated.
            // Could value types allow a pairing of line with line number ?
            AtomicInteger lineCounter = new AtomicInteger(streamData.linePointer);
            return streamData.dataStream.map(data -> this.toEvent(lineCounter, data));
        } catch (RuntimeException exc) {
            // Ensure the stream is closed in case of an Exception
            try (Stream<String> toClose = streamData.dataStream) {
                throw exc;
            }
        }
    }

    private Event toEvent(AtomicInteger lineCounter, String data) {
        try {
            lineCounter.incrementAndGet();
            return this.recordStructure.toEvent(data);
        } catch (ParseException e) {
            throw new RuntimeParseException(e.atLine(lineCounter.get()).inSource(storage.getReference()));
        }
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
