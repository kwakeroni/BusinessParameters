package be.kwakeroni.test.logging;

import org.mockito.ArgumentMatcher;
import org.slf4j.event.Level;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class LogMatcher implements ArgumentMatcher<LogEvent> {

    private List<Predicate<LogEvent>> expectations = new ArrayList<>();
    
    private LogMatcher() {
        
    }

    @Override
    public boolean matches(LogEvent argument) {
        for (Predicate<LogEvent> expectation : expectations) {
            if (!expectation.test(argument)) {
                return false;
            }
        }
        ;
        return true;
    }

    private LogMatcher withExpectation(Predicate<LogEvent> expectation) {
        this.expectations.add(expectation);
        return this;
    }

    public LogMatcher hasMessage(String message) {
        return withExpectation(event -> event.getFormattedMessage().equals(message));
    }

    public LogMatcher hasMessageContaining(String messagePart) {
        return withExpectation(event -> event.getFormattedMessage().contains(messagePart));
    }

    public LogMatcher hasLevel(Level level) {
        return withExpectation(event -> level.equals(event.getLevel()));
    }

    public LogMatcher hasLevelAbove(Level level) {
        return withExpectation(event -> event.getLevel().toInt() > level.toInt());
    }

    public LogMatcher hasLevelEqualToOrAbove(Level level) {
        return withExpectation(event -> event.getLevel().toInt() >= level.toInt());
    }

    public LogMatcher hasLevelBelow(Level level) {
        return withExpectation(event -> event.getLevel().toInt() < level.toInt());
    }

    public LogMatcher hasLevelEqualToOrBelow(Level level) {
        return withExpectation(event -> event.getLevel().toInt() <= level.toInt());
    }

    public static final class Factory {
        private Factory() {
        }
        
        private static LogMatcher matcher() {
            return new LogMatcher();
        }
        
        public static LogMatcher hasMessage(String message) {
            return matcher().hasMessage(message);
        }

        public static LogMatcher hasMessageContaining(String messagePart) {
            return matcher().hasMessageContaining(messagePart);
        }

        public static LogMatcher hasLevel(Level level) {
            return matcher().hasLevel(level);
        }

        public static LogMatcher hasLevelAbove(Level level) {
            return matcher().hasLevelAbove(level);
        }

        public static LogMatcher hasLevelEqualToOrAbove(Level level) {
            return matcher().hasLevelEqualToOrAbove(level);
        }

        public static LogMatcher hasLevelBelow(Level level) {
            return matcher().hasLevelBelow(level);
        }

        public static LogMatcher hasLevelEqualToOrBelow(Level level) {
            return matcher().hasLevelEqualToOrBelow(level);
        }
    }
}
