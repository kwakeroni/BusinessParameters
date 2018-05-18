package be.kwakeroni.test.logging;

import org.slf4j.Logger;
import org.slf4j.Marker;

abstract class SLF4JLoggerBase implements Logger {

    private static final Marker NO_MARKER = null;
    private static final Object[] NO_ARGS = new Object[0];

    private final String name;

    SLF4JLoggerBase(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public boolean isTraceEnabled() {
        return isTraceEnabled(NO_MARKER);
    }

    @Override
    public void trace(String s) {
        trace(NO_MARKER, s);
    }

    @Override
    public void trace(String s, Object o) {
        trace(NO_MARKER, s, o);
    }

    @Override
    public void trace(String s, Object o, Object o1) {
        trace(NO_MARKER, s, o, o1);
    }

    @Override
    public void trace(String s, Object... objects) {
        trace(NO_MARKER, s, objects);
    }

    @Override
    public void trace(String s, Throwable throwable) {
        trace(NO_MARKER, s, throwable);
    }

    @Override
    public void trace(Marker marker, String s) {
        trace(marker, s, NO_ARGS);
    }

    @Override
    public void trace(Marker marker, String s, Object o) {
        trace(marker, s, new Object[]{o});
    }

    @Override
    public void trace(Marker marker, String s, Object o, Object o1) {
        trace(marker, s, new Object[]{o, o1});
    }

    @Override
    public void trace(Marker marker, String s, Throwable throwable) {
        trace(marker, s, new Object[]{throwable});
    }

    @Override
    public boolean isDebugEnabled() {
        return isDebugEnabled(NO_MARKER);
    }

    @Override
    public void debug(String s) {
        debug(NO_MARKER, s);
    }

    @Override
    public void debug(String s, Object o) {
        debug(NO_MARKER, s, o);
    }

    @Override
    public void debug(String s, Object o, Object o1) {
        debug(NO_MARKER, s, o, o1);
    }

    @Override
    public void debug(String s, Object... objects) {
        debug(NO_MARKER, s, objects);
    }

    @Override
    public void debug(String s, Throwable throwable) {
        debug(NO_MARKER, s, throwable);
    }

    @Override
    public void debug(Marker marker, String s) {
        debug(marker, s, NO_ARGS);
    }

    @Override
    public void debug(Marker marker, String s, Object o) {
        debug(marker, s, new Object[]{o});
    }

    @Override
    public void debug(Marker marker, String s, Object o, Object o1) {
        debug(marker, s, new Object[]{o, o1});
    }

    @Override
    public void debug(Marker marker, String s, Throwable throwable) {
        debug(marker, s, new Object[]{throwable});
    }

    @Override
    public boolean isInfoEnabled() {
        return isInfoEnabled(NO_MARKER);
    }

    @Override
    public void info(String s) {
        info(NO_MARKER, s);
    }

    @Override
    public void info(String s, Object o) {
        info(NO_MARKER, s, o);
    }

    @Override
    public void info(String s, Object o, Object o1) {
        info(NO_MARKER, s, o, o1);
    }

    @Override
    public void info(String s, Object... objects) {
        info(NO_MARKER, s, objects);
    }

    @Override
    public void info(String s, Throwable throwable) {
        info(NO_MARKER, s, throwable);
    }

    @Override
    public void info(Marker marker, String s) {
        info(marker, s, NO_ARGS);
    }

    @Override
    public void info(Marker marker, String s, Object o) {
        info(marker, s, new Object[]{o});
    }

    @Override
    public void info(Marker marker, String s, Object o, Object o1) {
        info(marker, s, new Object[]{o, o1});
    }

    @Override
    public void info(Marker marker, String s, Throwable throwable) {
        info(marker, s, new Object[]{throwable});
    }

    @Override
    public boolean isWarnEnabled() {
        return isWarnEnabled(NO_MARKER);
    }

    @Override
    public void warn(String s) {
        warn(NO_MARKER, s);
    }

    @Override
    public void warn(String s, Object o) {
        warn(NO_MARKER, s, o);
    }

    @Override
    public void warn(String s, Object... objects) {
        warn(NO_MARKER, s, objects);
    }

    @Override
    public void warn(String s, Object o, Object o1) {
        warn(NO_MARKER, s, o, o1);
    }

    @Override
    public void warn(String s, Throwable throwable) {
        warn(NO_MARKER, s, throwable);
    }

    @Override
    public void warn(Marker marker, String s) {
        warn(marker, s, NO_ARGS);
    }

    @Override
    public void warn(Marker marker, String s, Object o) {
        warn(marker, s, new Object[]{o});
    }

    @Override
    public void warn(Marker marker, String s, Object o, Object o1) {
        warn(marker, s, new Object[]{o, o1});
    }

    @Override
    public void warn(Marker marker, String s, Throwable throwable) {
        warn(marker, s, new Object[]{throwable});
    }

    @Override
    public boolean isErrorEnabled() {
        return isErrorEnabled(NO_MARKER);
    }

    @Override
    public void error(String s) {
        error(NO_MARKER, s);
    }

    @Override
    public void error(String s, Object o) {
        error(NO_MARKER, s, o);
    }

    @Override
    public void error(String s, Object o, Object o1) {
        error(NO_MARKER, s, o, o1);
    }

    @Override
    public void error(String s, Object... objects) {
        error(NO_MARKER, s, objects);
    }

    @Override
    public void error(String s, Throwable throwable) {
        error(NO_MARKER, s, throwable);
    }

    @Override
    public void error(Marker marker, String s) {
        error(marker, s, NO_ARGS);
    }

    @Override
    public void error(Marker marker, String s, Object o) {
        error(marker, s, new Object[]{o});
    }

    @Override
    public void error(Marker marker, String s, Object o, Object o1) {
        error(marker, s, new Object[]{o, o1});
    }

    @Override
    public void error(Marker marker, String s, Throwable throwable) {
        error(marker, s, new Object[]{throwable});
    }
}
