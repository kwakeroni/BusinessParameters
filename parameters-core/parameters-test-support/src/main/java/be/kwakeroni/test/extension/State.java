package be.kwakeroni.test.extension;

import org.junit.jupiter.api.extension.ExtensionContext;

import java.util.function.Supplier;

enum State {
    ORIGINAL_CLASSLOADER;

    private static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create("be.kwakeroni", "test");

    public void set(ExtensionContext extensionContext, Object value) {
        if (extensionContext.getStore(NAMESPACE).get(name()) != null) {
            throw new IllegalStateException("Store not empty");
        }
        extensionContext.getStore(NAMESPACE).put(name(), value);
    }

    public <T> T get(ExtensionContext extensionContext) {
        return (T) extensionContext.getStore(NAMESPACE).get(name());

    }

    public <T> T get(ExtensionContext extensionContext, Class<T> type) {
        return extensionContext.getStore(NAMESPACE).get(name(), type);
    }

    public <T> T getOrCreate(ExtensionContext extensionContext, Supplier<T> supplier) {
        return (T) extensionContext.getStore(NAMESPACE).getOrComputeIfAbsent(name(), k -> supplier.get());
    }


}
