package be.kwakeroni.evelyn.client;

import be.kwakeroni.evelyn.model.Event;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.extension.mockito.MockitoExtension;
import org.mockito.Mock;

import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.AdditionalAnswers.answer;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CachingClientTableTest {

    @Mock
    private ClientTable<String> delegate;
    @Mock
    private Function<String, ClientOperation<String>> operationMapper;
    private CachingClientTable<String> table;

    @BeforeEach
    void setUp() {
        this.table = new CachingClientTable<>(delegate, operationMapper, Function.identity());
    }

    @Test
    @DisplayName("Gets name from delegate")
    void testGetName() {
        when(delegate.getName()).thenReturn("my.group");

        assertThat(table.getName()).isEqualTo("my.group");
    }

    @Test
    @DisplayName("Loads data only once")
    void testFindAll() {
        when(delegate.findAll()).thenAnswer(__ -> Stream.of("A", "B", "C"));

        assertThat(table.findAll()).containsOnly("A", "B", "C");
        assertThat(table.findAll()).containsOnly("A", "B", "C");

        verify(delegate, times(1)).findAll();
        verifyNoMoreInteractions(delegate);
    }

    @Test
    @DisplayName("Loads all data into cache when retrieving one entry")
    void testFindById() {
        when(delegate.findAll()).thenAnswer(__ -> Stream.of("A", "B", "C"));

        assertThat(table.findById("A")).isEqualTo("A");
        assertThat(table.findById("B")).isEqualTo("B");
        assertThat(table.findById("C")).isEqualTo("C");

        verify(delegate, times(1)).findAll();
        verifyNoMoreInteractions(delegate);
    }

    @Nested
    @DisplayName("Appends events")
    class AppendTest {

        @Mock
        Event event;
        @Mock
        ClientOperation<String> replace;
        @Mock
        ClientOperation<String> add;

        @BeforeEach
        public void setUp() {
            when(delegate.append(anyString(), anyString(), anyString(), anyString())).thenReturn(event);
            when(event.getObjectId()).thenReturn("myId");
            when(event.getOperation()).thenReturn("REPLACE");
            when(operationMapper.apply("REPLACE")).thenReturn(replace);
            when(operationMapper.apply("ADD")).thenReturn(add);
        }

        @Test
        @DisplayName("By delegating")
        void testAppendDelegate() {
            table.append("myUser", "REPLACE", "myId", "myData");

            verify(delegate).append("myUser", "REPLACE", "myId", "myData");
        }

        @Test
        @DisplayName("By applying the event on the cache")
        void testAppendApplyOnCache() {
            table.append("myUser", "REPLACE", "myId", "myData");

            verify(operationMapper).apply("REPLACE");
            verify(replace).operate(null, event);
        }

        @Test
        @DisplayName("After loading the cache")
        void testAppendApplyAfterCacheLoad() {
            // Ensure that the event is not appended before the cache is loaded
            // (this would cause the event to be applied twice
            AtomicReference<String> myId = new AtomicReference<>("myId");
            when(delegate.findAll()).thenAnswer(__ -> Stream.of("A", "B", "C", myId.get()));
            when(delegate.append(anyString(), anyString(), anyString(), anyString())).then(
                    answer((user, operation, id, data) -> {
                        myId.set("replacedId");
                        return event;
                    }));

            table.append("myUser", "REPLACE", "myId", "myData");

            verify(operationMapper).apply("REPLACE");
            verify(replace).operate("myId", event);
        }

        @Test
        @DisplayName("Adding new entities to the cache")
        void testAppendAddToCache() {
            AtomicReference<String[]> entities = new AtomicReference<>(new String[]{"A", "B", "C"});
            when(event.getOperation()).thenReturn("ADD");
            when(delegate.findAll()).thenAnswer(__ -> Stream.of(entities.get()));
            when(delegate.append(anyString(), anyString(), anyString(), anyString())).then(
                    answer((user, operation, id, data) -> {
                        entities.set(new String[]{"A", "B", "C", "myId"});
                        return event;
                    }));
            when(add.operate(any(), any())).thenReturn("myId");

            table.append("myUser", "ADD", "myId", "myData");

            assertThat(table.findById("myId")).isEqualTo("myId");

            verify(operationMapper).apply("ADD");
            verify(add).operate(null, event);
        }

    }

}