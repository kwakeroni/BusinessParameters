package be.kwakeroni.evelyn.model.test;

import be.kwakeroni.evelyn.storage.Storage;

import java.util.Arrays;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public final class TestStorage {

    private TestStorage() {

    }

    public static Storage asStorage(String... contents) {
        return asStorage(null, contents);
    }

    public static Storage asStorage(SilentCloseable resource, String... contents) {
        Storage storage = mock(Storage.class);
        when(storage.read()).thenAnswer(no ->
                Arrays.stream(contents)
                        .onClose(() -> {
                            if (resource != null) {
                                resource.close();
                            }
                        }));
        return storage;
    }


}
