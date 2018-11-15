package be.kwakeroni.test.extension;

import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.BeforeTestExecutionCallback;
import org.junit.jupiter.api.extension.Extension;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestInstancePostProcessor;

import java.util.Optional;

public class LifeCycleLogger implements Extension,
        TestInstancePostProcessor,
        BeforeAllCallback, BeforeEachCallback, BeforeTestExecutionCallback,
        AfterTestExecutionCallback, AfterEachCallback, AfterAllCallback {

    public static void log(String msg) {
        System.out.println("[" + Thread.currentThread().getId() + "] " + msg);
    }

    @Override
    public void afterAll(ExtensionContext context) {
        log("After  All   " + context.getTestInstance());
    }

    @Override
    public void afterEach(ExtensionContext context) {
        log("After  Each  " + context.getTestInstance());
    }

    @Override
    public void afterTestExecution(ExtensionContext context) {
        log("After  Exec  " + context.getTestInstance());
    }

    @Override
    public void beforeAll(ExtensionContext context) {
        log("Before All   " + context.getTestInstance());
    }

    @Override
    public void beforeEach(ExtensionContext context) {
        log("Before Each  " + context.getTestInstance());
    }

    @Override
    public void beforeTestExecution(ExtensionContext context) {
        log("Before Exec  " + context.getTestInstance());
    }

    @Override
    public void postProcessTestInstance(Object testInstance, ExtensionContext context) {
        log("Post Process " + Optional.ofNullable(testInstance));
    }
}
