package be.kwakeroni.test.logging;

import org.slf4j.ILoggerFactory;
import org.slf4j.Logger;

public class SLF4JTestLoggerFactory implements ILoggerFactory {

    @Override
    public Logger getLogger(String name) {
        return new SLF4JTestLogger(name);
    }

}
