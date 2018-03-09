package be.kwakeroni.test.logging;

import org.slf4j.event.Level;

import java.util.function.Predicate;

public class ConsoleLogger implements LoggerSpy {

    private final Predicate<Level> isLogEnabled;

    public ConsoleLogger(Predicate<Level> isLogEnabled) {
        this.isLogEnabled = isLogEnabled;
    }

    @Override
    public void log(LogEvent event) {
        if (isLogEnabled.test(event.getLevel())) {
            System.out.println(String.format(
                    getMessagePattern(),
                    event.getLevel().name(),
                    event.getLoggerTail(1),
                    event.getFormattedMessage()
            ));
        }
    }

    private String getMessagePattern() {
        return "%5s %30s %s";
    }
}
