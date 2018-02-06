package be.kwakeroni.evelyn.model;

import java.util.Optional;
import java.util.OptionalInt;

public class ParseException extends Exception {

    private String source = null;
    private Integer line = null;
    private Integer position = null;

    public ParseException(String message) {
        super(message);
    }

    @Override
    public String getMessage() {
        String message = super.getMessage();
        if (line != null) {
            if (position != null) {
                message += " at position " + line + ":" + position;
            } else {
                message += " at line " + line;
            }
        }
        if (source != null) {
            message += " in source " + source;
        }
        return message;
    }

    public ParseException atLine(int line) {
        this.line = line;
        return this;
    }

    public ParseException atPosition(int pos) {
        this.position = pos;
        return this;
    }

    public ParseException inSource(String source) {
        this.source = source;
        return this;
    }

    public OptionalInt getLine() {
        return (line == null) ? OptionalInt.empty() : OptionalInt.of(line);
    }

    public OptionalInt getPosition() {
        return (position == null) ? OptionalInt.empty() : OptionalInt.of(position);
    }

    public Optional<String> getSource() {
        return Optional.ofNullable(source);
    }
}
