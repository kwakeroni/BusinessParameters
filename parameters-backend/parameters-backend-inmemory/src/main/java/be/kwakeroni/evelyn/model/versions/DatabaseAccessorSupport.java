package be.kwakeroni.evelyn.model.versions;

import be.kwakeroni.evelyn.model.DatabaseAccessor;
import be.kwakeroni.evelyn.model.parser.ParseException;
import be.kwakeroni.evelyn.storage.Storage;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

public class DatabaseAccessorSupport implements DatabaseAccessor {

    private static final String ATTRIBUTE_VERSION = "version";
    private static final String ATTRIBUTE_DB_NAME = "name";

    private final String version;
    private final String databaseName;
    private final Storage storage;
    private final Map<String, String> attributes;

    public DatabaseAccessorSupport(String version, String databaseName, Storage storage) {
        this(version, databaseName, storage, new HashMap<>());
    }

    private DatabaseAccessorSupport(String version, String databaseName, Storage storage, Map<String, String> attributes) {
        this.version = Objects.requireNonNull(version, "version");
        this.databaseName = Objects.requireNonNull(databaseName, "databaseName");
        this.storage = Objects.requireNonNull(storage, "storage");
        this.attributes = Objects.requireNonNull(attributes, "attributes");
    }

    public static DatabaseAccessorSupport createFrom(Storage storage) throws ParseException {
        Map<String, String> attributes = readAttributes(storage);
        return new DatabaseAccessorSupport(attributes.get(ATTRIBUTE_VERSION), attributes.get(ATTRIBUTE_DB_NAME), storage);
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
        this.storage.writeHeader(version);
    }

    public String getAttribute(String name) {
        return this.attributes.get(name);
    }

    private static Map<String, String> readAttributes(Storage storage) throws ParseException {
        Map<String, String> attributes = new HashMap<>();
        try (Stream<String> stream = storage.read()) {
            int line = 1;

            try {

                Iterator<String> iterator = checkNotEmpty(stream.iterator());
                checkHeader(iterator.next());

                while (iterator.hasNext()) {
                    line++;
                    String string = checkAttributeLine(iterator.next());
                    String[] array = checkAttributeSplit(string.substring(1).split("="));
                    checkPutAttribute(attributes, array[0].trim(), array[1].trim());
                }
            } catch (ParseException exc) {
                throw exc.atLine(line);
            }
        }
        checkRequiredAttributes(attributes);
        return Collections.unmodifiableMap(attributes);
    }

    private static Iterator<String> checkNotEmpty(Iterator<String> iterator) throws ParseException {
        if (!iterator.hasNext()) {
            throw new ParseException("Unexpected end of input").atPosition(1);
        }
        return iterator;
    }

    private static void checkHeader(String string) throws ParseException {
        if (!"!evelyn-db".equals(string)) {
            throw new ParseException("Input not recognized").atLine(1);
        }
    }

    private static String checkAttributeLine(String string) throws ParseException {
        if (!string.startsWith("!")) {
            throw new ParseException("Unexpected data").atPosition(1);
        }
        return string;
    }

    private static String[] checkAttributeSplit(String[] array) throws ParseException {
        if (array.length == 1) {
            // No "="
            throw new ParseException("Expected attribute value").atPosition(1 + array[0].length());
        }
        if (array.length > 2) {
            // Multiple "="
            throw new ParseException("Unexpected character '='").atPosition(1 + array[0].length() + 1 + array[1].length() + 1);
        }
        return array;
    }

    private static void checkPutAttribute(Map<String, String> attributes, String key, String value) throws ParseException {
        if (attributes.containsKey(key)) {
            throw new ParseException("Duplicate attribute: " + key);
        }
        attributes.put(key, value);
    }

    private static void checkRequiredAttributes(Map<String, String> attributes) throws ParseException {
        checkRequiredAttributes(attributes, ATTRIBUTE_VERSION, ATTRIBUTE_DB_NAME);
    }

    private static void checkRequiredAttributes(Map<String, String> attributes, String... required) throws ParseException {
        for (String attribute : required) {
            if (!attributes.containsKey(attribute)) {
                throw new ParseException("Unspecified attribute: " + attribute);
            }
        }
    }


}
