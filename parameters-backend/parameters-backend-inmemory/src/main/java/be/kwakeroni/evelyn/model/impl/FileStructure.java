package be.kwakeroni.evelyn.model.impl;

import be.kwakeroni.evelyn.model.ParseException;
import be.kwakeroni.evelyn.storage.Storage;
import be.kwakeroni.evelyn.storage.StorageExistsException;
import be.kwakeroni.evelyn.storage.impl.HeaderStructure;

import java.nio.charset.Charset;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

class FileStructure {

    private static final FileStructure INSTANCE = new FileStructure();

    private FileStructure() {

    }

    public static FileStructure getInstance() {
        return INSTANCE;
    }

    public StreamData readData(Storage storage, Charset charset) throws ParseException {
        return parse(storage, charset, null);
    }

    public Map<String, String> readAttributes(Storage storage) throws ParseException {
        Map<String, String> attributes = new HashMap<>();
        AttributeHandler handler = (key, value) -> checkPutAttribute(attributes, key, value);

        try (Stream<String> dataStream = parse(storage, Charset.defaultCharset(), handler).dataStream) {
            return Collections.unmodifiableMap(attributes);
        }
    }

    public void initializeStorage(Storage storage, Map<String, String> attributes, String versionAttribute) throws StorageExistsException {
        String version = Objects.requireNonNull(attributes.get(versionAttribute), "version not specified");
        storage.writeHeader(version);

        attributes.forEach((key, value) -> {
            if (!versionAttribute.equals(key)) {
                storage.append("!" + key + "=" + value);
            }
        });

        storage.append("!data");
    }

    private StreamData parse(Storage storage, Charset charset, AttributeHandler attributeHandler) throws ParseException {
        try (CloseableStreamIterator<String> iterator = new CloseableStreamIterator<>(() -> storage.read(charset))) {

            int line = HeaderStructure.readToAttributes(iterator);

            try {

                while (iterator.hasNext()) {
                    line++;
                    String string = checkAttributeLine(iterator.next());
                    if (isDataStart(string)) {
                        break;
                    }
                    if (attributeHandler != null) {
                        String[] array = checkAttributeSplit(string.substring(1).split("="));
                        attributeHandler.put(array[0].trim(), array[1].trim());
                    }
                }

                return new StreamData(iterator.getRemainingStream(), line);

            } catch (ParseException exc) {
                throw exc.atLine(line);
            }
        } catch (ParseException exc) {
            throw exc.inSource(storage.getReference());
        }
    }

    private String checkAttributeLine(String string) throws ParseException {
        if (!string.startsWith("!")) {
            throw new ParseException("Unexpected data").atPosition(1);
        }
        return string;
    }

    private String[] checkAttributeSplit(String[] array) throws ParseException {
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

    private void checkPutAttribute(Map<String, String> attributes, String key, String value) throws ParseException {
        if (attributes.containsKey(key)) {
            throw new ParseException("Duplicate attribute: " + key);
        }
        attributes.put(key, value);
    }

    private boolean isDataStart(String line) {
        return "!data".equals(line.trim());
    }

    private interface AttributeHandler {
        void put(String attribute, String value) throws ParseException;
    }

    public static class StreamData {
        public final Stream<String> dataStream;
        public final int linePointer;

        StreamData(Stream<String> dataStream, int linePointer) {
            this.dataStream = dataStream;
            this.linePointer = linePointer;
        }
    }

}
