package be.kwakeroni.evelyn.test;

import be.kwakeroni.evelyn.model.Event;
import be.kwakeroni.evelyn.storage.Storage;

import java.time.LocalDateTime;
import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public final class TestModel {
    public static final String STORAGE_SOURCE = "TestModel$Storage";

    private TestModel() {

    }

    public static Storage asStorage(String... contents) {
        return asStorage(null, contents);
    }

    public static Storage asStorage(SilentCloseable resource, String... contents) {
        Storage storage = mock(Storage.class);

        when(storage.getReference()).thenReturn(STORAGE_SOURCE);

        when(storage.read(any())).thenAnswer(no ->
                Arrays.stream(contents)
                        .onClose(() -> {
                            if (resource != null) {
                                resource.close();
                            }
                        }));
        return storage;
    }


    public static Event event(String objectId) {
        return event("anonymous", objectId, "INSERT", "abc", LocalDateTime.now());
    }

    public static Event event(String user, String objectId, String operation, String data, LocalDateTime time) {
        return new Event() {
            @Override
            public LocalDateTime getTime() {
                return time;
            }

            @Override
            public String getObjectId() {
                return objectId;
            }

            @Override
            public String getUser() {
                return user;
            }

            @Override
            public String getOperation() {
                return operation;
            }

            @Override
            public String getData() {
                return data;
            }

            @Override
            public String toString() {
                return "event{" +
                        "objectId='" + objectId + '\'' +
                        ", operation='" + operation + '\'' +
                        ", data='" + data + '\'' +
                        '}';
            }
        };
    }

}
