package be.kwakeroni.evelyn.model;

import java.util.Optional;
import java.util.OptionalInt;

public class ParseException extends Exception {

    private Optional<String> source = Optional.empty();
    private OptionalInt line = OptionalInt.empty();
    private OptionalInt position = OptionalInt.empty();

    public ParseException(String message) {
        super(message);
    }

    @Override
    public String getMessage() {
        String message = super.getMessage();
        if (line.isPresent()) {
            if (position.isPresent()) {
                message += " at position " + line.getAsInt() + ":" + position.getAsInt();
            } else {
                message += " at line " + line.getAsInt();
            }
        }
        if (source.isPresent()) {
            message += " in source " + source.get();
        }
        return message;
    }

    public ParseException atLine(int line) {
        this.line = OptionalInt.of(line);
        return this;
    }

    public ParseException atPosition(int pos) {
        this.position = OptionalInt.of(pos);
        return this;
    }

    public ParseException inSource(String source) {
        this.source = Optional.of(source);
        return this;
    }

    public OptionalInt getLine() {
        return line;
    }

    public OptionalInt getPosition() {
        return position;
    }

    public Optional<String> getSource() {
        return source;
    }
}
