package be.kwakeroni.test.logging;

import org.slf4j.Marker;
import org.slf4j.event.Level;
import org.slf4j.helpers.FormattingTuple;
import org.slf4j.helpers.MessageFormatter;

public final class LogEvent {

    private final String logger;
    private final Level level;
    private final Marker marker;
    private final String message;
    private final Object[] arguments;
    private final FormattingTuple formattedMessage;

    public LogEvent(String logger, Level level, Marker marker, String message, Object... arguments) {
        this.logger = logger;
        this.level = level;
        this.marker = marker;
        this.message = message;
        this.arguments = arguments;
        this.formattedMessage = MessageFormatter.arrayFormat(message, arguments);
    }

    public String getLogger() {
        return logger;
    }

    public String getLoggerTail(int n) {
        int dot = logger.length();
        while (n-- > 0 && dot > 0) {
            dot = logger.lastIndexOf('.', dot);
        }
        return (dot >= 0) ? logger.substring(dot + 1) : logger;
    }

    public Level getLevel() {
        return level;
    }

    public Marker getMarker() {
        return marker;
    }

    public String getMessage() {
        return message;
    }

    public Object[] getArguments() {
        return arguments;
    }

    public String getFormattedMessage() {
        return formattedMessage.getMessage();
    }
}
