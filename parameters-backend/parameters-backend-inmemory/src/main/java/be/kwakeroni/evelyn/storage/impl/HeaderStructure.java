package be.kwakeroni.evelyn.storage.impl;

import be.kwakeroni.evelyn.model.ParseException;

import java.util.Iterator;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Stream;

public final class HeaderStructure {

    private static final String HEADER_VERSION = "!version=";
    private static final String HEADER_PROLOG = "!evelyn-db";

    private HeaderStructure() {

    }

    public static void writeHeader(String version, Consumer<String> sink) {
        sink.accept(HEADER_PROLOG);
        sink.accept(HEADER_VERSION + version);
    }

    public static String readVersion(Supplier<Stream<String>> supplier) throws ParseException {
        String[] array = supplier.get()
                .limit(2)
                .toArray(String[]::new);

        checkNotEmpty(array);
        checkHeader(array[0]);
        checkTwoLines(array);
        checkVersionAttribute(array[1]);

        return readVersionFromAttribute(array[1]);
    }

    public static int readToAttributes(Iterator<String> iterator) throws ParseException {
        checkNotEmpty(iterator);
        checkHeader(iterator.next());
        return 1;
    }

    private static String readVersionFromAttribute(String attributeLine) {
        return attributeLine.substring(HEADER_VERSION.length()).trim();
    }

    private static void checkNotEmpty(String[] lines) throws ParseException {
        if (lines.length == 0) {
            throw new ParseException("Unexpected end of input").atLine(1).atPosition(1);
        }
    }

    private static void checkTwoLines(String[] lines) throws ParseException {
        if (lines.length < 2) {
            throw new ParseException("Unexpected end of input").atLine(2).atPosition(1);
        }
    }

    private static void checkNotEmpty(Iterator<String> iterator) throws ParseException {
        if (!iterator.hasNext()) {
            throw new ParseException("Unexpected end of input").atLine(1).atPosition(1);
        }
    }

    private static void checkHeader(String string) throws ParseException {
        if (!HEADER_PROLOG.equals(string)) {
            throw new ParseException("Input not recognized").atLine(1);
        }
    }

    private static void checkVersionAttribute(String attributeLine) throws ParseException {
        if (!attributeLine.startsWith(HEADER_VERSION)) {
            throw new ParseException("Input not recognized").atLine(2);
        }
    }

}
