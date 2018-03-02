package be.kwakeroni.evelyn.client;

import be.kwakeroni.evelyn.model.DatabaseAccessor;
import be.kwakeroni.evelyn.model.Event;
import be.kwakeroni.evelyn.model.ParseException;
import be.kwakeroni.evelyn.model.RuntimeParseException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.extension.mockito.MockitoExtension;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.function.Function;
import java.util.stream.Stream;

import static be.kwakeroni.evelyn.test.Assertions.assertThat;
import static be.kwakeroni.evelyn.test.TestModel.event;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@DisplayName("Supports client tables")
@ExtendWith(MockitoExtension.class)
public class DefaultClientTableTest {

    @Mock
    private DatabaseAccessor accessor;
    @Mock
    private Function<String, ClientOperation<TestEntity>> operationMap;
    @Mock
    private ClientOperation<TestEntity> add;
    @Mock
    private ClientOperation<TestEntity> replace;
    @Mock
    private TestEntity added1;
    @Mock
    private TestEntity replaced1;
    @Mock
    private TestEntity added2;
    private Event event1 = event(null, "abc-123", "ADD", "DATA1", null);
    private Event event2 = event(null, "def-456", "ADD", "data2", null);
    private Event event3 = event(null, "abc-123", "REPLACE", "data3", null);
    private LocalDateTime now;
    private DefaultClientTable<TestEntity> table;


    @BeforeEach
    public void setUpTable() throws Exception {
        Instant instant = Instant.now();
        Clock clock = Clock.fixed(instant, ZoneId.systemDefault());

        this.now = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
        this.table = new DefaultClientTable<>(accessor, operationMap, clock);

        when(operationMap.apply("ADD")).thenReturn(add);
        when(operationMap.apply("REPLACE")).thenReturn(replace);
        when(add.operate(any(), any())).thenReturn(added1, added2);
        when(replace.operate(any(), any())).thenReturn(replaced1);

        when(accessor.getData()).thenAnswer(inv -> Stream.of(event1, event2, event3));
    }

    @Test
    @DisplayName("With a name")
    public void testGetName() {
        when(accessor.getDatabaseName()).thenReturn("MyTableName");

        assertThat(table.getName()).isEqualTo("MyTableName");
    }

    @Test
    @DisplayName("Appends operations")
    public void testAppend() {

        table.append("myUser", "ADD", "myObject", "myData");

        ArgumentCaptor<Event> event = ArgumentCaptor.forClass(Event.class);
        verify(accessor).append(event.capture());

        assertThat(event.getValue().getTime()).isEqualTo(now);
        assertThat(event.getValue().getUser()).isEqualTo("myUser");
        assertThat(event.getValue().getOperation()).isEqualTo("ADD");
        assertThat(event.getValue().getObjectId()).isEqualTo("myObject");
        assertThat(event.getValue().getData()).isEqualTo("myData");
    }

    @Nested
    @DisplayName("Retrieves a client entity by id")
    class FindByIdTest {

        @Test
        @DisplayName("by applying operations")
        public void testFindById() {

            TestEntity entity = table.findById("abc-123");

            assertThat(entity).isSameAs(replaced1);

            InOrder inOrder = Mockito.inOrder(operationMap, add, replace);
            inOrder.verify(add).operate(null, event1);
            inOrder.verify(add, never()).operate(null, event2);
            inOrder.verify(replace).operate(added1, event3);
        }

        @Test
        @DisplayName("propagating parsing exceptions")
        public void testFindByIdWithException() throws Exception {
            ParseException exception = new ParseException("Test");
            when(accessor.getData()).thenThrow(exception);

            assertThatThrownBy(() -> table.findById("abc"))
                    .isInstanceOf(RuntimeParseException.class)
                    .hasCause(exception);
        }

        @Test
        @DisplayName("closing the event stream")
        public void testFindByIdWithClosedStream(@Mock Stream<Event> stream) throws Exception {
            when(accessor.getData()).thenAnswer(inv -> stream);

            table.findById("abc");

            verify(stream).close();
        }

        @Test
        @DisplayName("not supporting parallel streams")
        public void testFindByIdWithParallelStream() throws Exception {
            when(accessor.getData()).thenAnswer(inv -> Stream.of(event1, event2, event3).parallel());

            assertThatThrownBy(() -> table.findById("abc"))
                    .isInstanceOf(UnsupportedOperationException.class)
                    .hasMessageContaining("Parallel");
        }

    }

    @Nested
    @DisplayName("Retrieves all client entities")
    class FindAllTest {
        @Test
        @DisplayName("by applying operations")
        public void testFindAll() {

            Stream<TestEntity> entities = table.findAll();

            assertThat(entities).containsOnly(replaced1, added2);

            InOrder inOrder = Mockito.inOrder(operationMap, add, replace);
            inOrder.verify(add).operate(null, event1);
            inOrder.verify(add).operate(null, event2);
            inOrder.verify(replace).operate(added1, event3);
        }

        @Test
        @DisplayName("propagating parsing exceptions")
        public void testFindAllWithException() throws Exception {
            ParseException exception = new ParseException("Test");
            when(accessor.getData()).thenThrow(exception);

            assertThatThrownBy(() -> table.findAll())
                    .isInstanceOf(RuntimeParseException.class)
                    .hasCause(exception);
        }

        @Test
        @DisplayName("not supporting parallel streams")
        public void testFindAllWithParallelStream() throws Exception {
            when(accessor.getData()).thenAnswer(inv -> Stream.of(event1, event2, event3).parallel());

            assertThatThrownBy(() -> table.findAll())
                    .isInstanceOf(UnsupportedOperationException.class)
                    .hasMessageContaining("Parallel");
        }

        @Test
        @DisplayName("closing stream in case of exception")
        public void testFindAllWithStreamException(@Mock Stream<Event> stream) throws Exception {
            when(accessor.getData()).thenReturn(stream);
            when(stream.collect(any())).thenThrow(new UnsupportedOperationException("Test"));

            assertThatThrownBy(() -> table.findAll())
                    .isInstanceOf(UnsupportedOperationException.class);

            verify(stream).close();
        }
    }

    private static interface TestEntity {

    }
}