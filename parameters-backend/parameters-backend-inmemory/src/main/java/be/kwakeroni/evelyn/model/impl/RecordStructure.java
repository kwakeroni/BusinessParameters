package be.kwakeroni.evelyn.model.impl;

import be.kwakeroni.evelyn.model.Event;
import be.kwakeroni.evelyn.model.ParseException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class RecordStructure {

    private static RecordStructure INSTANCE = new RecordStructure();
    private static DateTimeFormatter FORMAT = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    public static RecordStructure getInstance() {
        return INSTANCE;
    }

    private RecordStructure() {

    }

    private static final Pattern ESCAPE_DETECT_PATTERN = Pattern.compile("(\\r\\n?|\\n|\\|)");
    private static final Pattern ESCAPE_PATTERN = Pattern.compile("(\\r\\n?|\\n|\\\\|\\|)");

    public String toData(Event event) {

        String data = event.getData();
        boolean isEscaping = ESCAPE_DETECT_PATTERN.matcher(data).find();

        StringBuffer buffer = (isEscaping) ? new StringBuffer("\\") : new StringBuffer(" ");

        buffer.append(event.getTime().format(FORMAT)).append('|');
        buffer.append(check(event.getObjectId())).append('|');
        buffer.append(check(event.getUser())).append('|');
        buffer.append(check(event.getOperation())).append('|');

        if (isEscaping) {
            Matcher matcher = ESCAPE_PATTERN.matcher(data);
            while (matcher.find()) {
                if ("\n".equals(matcher.group(1))
                        || "\r\n".equals(matcher.group(1))
                        || "\r".equals(matcher.group(1))) {
                    matcher.appendReplacement(buffer, "\\\\n");
                } else {
                    matcher.appendReplacement(buffer, "\\\\$1");
                }
            }
            matcher.appendTail(buffer);
        } else {
            buffer.append(data);
        }


        return buffer.toString();
    }


    private String check(String field) {
        return field;
    }

    public Event toEvent(String data) throws ParseException {
        String[] split = splitAndUnescape(data);

        if (split.length != 5) {
            throw new ParseException("Unexpected number of columns: " + split.length);
        }

        return new Event() {

            public String getTimestamp() {
                return split[0];
            }

            @Override
            public LocalDateTime getTime() {
                return LocalDateTime.parse(getTimestamp(), FORMAT);
            }

            @Override
            public String getUser() {
                return split[2];
            }

            @Override
            public String getObjectId() {
                return split[1];
            }

            @Override
            public String getOperation() {
                return split[3];
            }

            @Override
            public String getData() {
                return split[4];
            }
        };
    }

    private static final Pattern SPLIT_PATTERN = Pattern.compile("(?<!\\\\)\\|");
    private static final Pattern UNESCAPE_PATTERN = Pattern.compile("\\\\([n|\\\\])");

    private String[] splitAndUnescape(String data) {
        if (data.startsWith("\\")) {
            String[] array = SPLIT_PATTERN.split(data);
            // Remove leading space
            array[0] = array[0].substring(1);
            // Unescape data
            array[4] = unescape(array[4]);

            return array;
        } else {
            // Not escaped.
            String[] array = data.split("\\|");
            // Remove leading space
            array[0] = array[0].substring(1);

            return array;
        }
    }

    private String unescape(String string) {
        //array[4] = array[4].replaceAll("\\\\(\\\\|\\|)", "$1");
        Matcher matcher = UNESCAPE_PATTERN.matcher(string);
        StringBuffer buffer = new StringBuffer();
        while (matcher.find()) {
            String escaped = matcher.group(1);
            if ("n".equals(escaped)) {
                matcher.appendReplacement(buffer, System.lineSeparator());
            } else {
                matcher.appendReplacement(buffer, "$1");
            }
        }
        matcher.appendTail(buffer);
        return buffer.toString();
    }
}
