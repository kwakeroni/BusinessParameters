package be.kwakeroni.test.logging;

import org.slf4j.Marker;
import org.slf4j.event.Level;

import static org.slf4j.event.Level.*;

class SLF4JTestLogger extends SLF4JLoggerBase {

    SLF4JTestLogger(String name) {
        super(name);
    }

    private boolean isLogEnabled(Level level) {
        return SLF4JTestLog.isLogEnabled(level);
    }

    private void log(Level level, Marker marker, String message, Object... arguments) {
        SLF4JTestLog.log(new LogEvent(getName(), level, marker, message, arguments));
    }

    @Override
    public boolean isTraceEnabled(Marker marker) {
        return isLogEnabled(TRACE);
    }

    @Override
    public void trace(Marker marker, String s, Object... objects) {
        log(TRACE, marker, s, objects);
    }

    @Override
    public boolean isDebugEnabled(Marker marker) {
        return isLogEnabled(DEBUG);
    }

    @Override
    public void debug(Marker marker, String s, Object... objects) {
        log(DEBUG, marker, s, objects);
    }

    @Override
    public boolean isInfoEnabled(Marker marker) {
        return isLogEnabled(INFO);
    }

    @Override
    public void info(Marker marker, String s, Object... objects) {
        log(INFO, marker, s, objects);
    }

    @Override
    public boolean isWarnEnabled(Marker marker) {
        return isLogEnabled(WARN);
    }

    @Override
    public void warn(Marker marker, String s, Object... objects) {
        log(WARN, marker, s, objects);
    }

    @Override
    public boolean isErrorEnabled(Marker marker) {
        return isLogEnabled(ERROR);
    }

    @Override
    public void error(Marker marker, String s, Object... objects) {
        log(ERROR, marker, s, objects);
    }
}
