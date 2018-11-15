package be.kwakeroni.test.extension;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(LifeCycleLogger.class)
public class LifecycleLoggerTest {

    @Test
    public void test1() {
        LifeCycleLogger.log("Running test1");
    }

    @Test
    public void test2() {
        LifeCycleLogger.log("Running test2");
    }

    @Nested
    class NestedTest {
        @Test
        public void test1() {
            LifeCycleLogger.log("Running nested1");
        }

        @Test
        public void test2() {
            LifeCycleLogger.log("Running nested2");
        }

    }

    @Nested
    class RepeatedTest {
        @org.junit.jupiter.api.RepeatedTest(3)
        public void test() {
            LifeCycleLogger.log("Running repeated");
        }

    }
}
