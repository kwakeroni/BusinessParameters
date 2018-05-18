package be.kwakeroni.test.logging;

import org.mockito.Mockito;
import org.slf4j.event.Level;

import java.util.function.Supplier;

public final class SLF4JTestLog {

    private static Supplier<LoggerSpy> SPY_SUPPLIER = () -> new ConsoleLogger(SLF4JTestLog::isDisplayEnabled);
    private static ThreadLocal<State> state = ThreadLocal.withInitial(State::new);
    private static ThreadLocal<State> backupState = new ThreadLocal<>();

    private SLF4JTestLog() {
    }

    public static Level getLogLevel() {
        return getReadState().logLevel;
    }

    public static void setLogLevel(Level level) {
        getWriteState().logLevel = level;
    }

    public static Level getDisplayLevel() {
        return getReadState().displaylevel;
    }

    public static void setDisplayLevel(Level level) {
        getWriteState().displaylevel = level;
    }

    public static void setLevel(Level level) {
        setLogLevel(level);
        setDisplayLevel(level);
    }

    public static boolean isLogEnabled(Level level) {
        return getLogLevel().toInt() <= level.toInt();
    }

    public static boolean isDisplayEnabled(Level level) {
        return getDisplayLevel().toInt() <= level.toInt();
    }

    public static void setDefaultSpySupplier(Supplier<LoggerSpy> supplier) {
        SPY_SUPPLIER = supplier;
    }

    public static void setSpy(LoggerSpy spy) {
        getWriteState().spy = spy;
    }

    public static LoggerSpy loggerSpy() {
        // Using write state here to isolate the logging within the test
        return getWriteState().spy;
    }

    public static LoggerSpy loggerSpy(Level level) {
        setLevel(level);
        return loggerSpy();
    }

    static void log(LogEvent event) {
        if (isLogEnabled(event.getLevel())) {
            getReadState().spy.log(event);
        }
    }

    public static void reset() {
        state.set(backupState.get());
        backupState.remove();
    }

    private static State getReadState() {
        return state.get();
    }

    private static State getWriteState() {
        if (backupState.get() == null) {
            State current = state.get();
            backupState.set(current);
            state.set(new State(current));
        }
        return state.get();
    }

    private static class State {
        Level logLevel;
        Level displaylevel;
        LoggerSpy spy;

        State() {
            this.logLevel = Level.TRACE;
            this.displaylevel = Level.ERROR;
            this.spy = defaultSpy();
        }

        State(State copy) {
            this.logLevel = copy.logLevel;
            this.displaylevel = copy.displaylevel;
            this.spy = defaultSpy();
        }
    }

    private static LoggerSpy defaultSpy() {
        return Mockito.spy(SPY_SUPPLIER.get());
    }

}
