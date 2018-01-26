package be.kwakeroni.evelyn.model.impl;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.extension.mockito.MockitoExtension;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.stubbing.Answer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
@DisplayName("CloseableStreamIterator")
public class CloseableStreamIteratorTest {

    private static final String[] DATA = new String[]{"A", "B", "C", "D", "E"};

    @Mock
    SilentCloseable resource;
    @Mock
    Consumer<Iterator<?>> toStream;

    private CloseableStreamIterator<String> newInstance() {
        return newInstance(UnaryOperator.identity());
    }

    private CloseableStreamIterator<String> newInstance(UnaryOperator<Stream<String>> operator) {
        return new CloseableStreamIteratorForTest<>(() ->
                operator.apply(Stream.of(DATA).onClose(resource::close))
        );
    }

    @Test
    @DisplayName("can be iterated over until the end")
    public void testFullIterator() {
        CloseableStreamIterator<String> iter = newInstance();

        for (int i = 0; i < DATA.length; i++) {
            assertThat(iter.hasNext()).isTrue();
            assertThat(iter.next()).isEqualTo(DATA[i]);
        }
        assertThat(iter.hasNext()).isFalse();
    }

    @Test
    @DisplayName("can pick up streaming after partial iteration")
    public void testGetRemainingStream() {
        CloseableStreamIterator<String> iter = newInstance();

        int splitPosition = 2;

        for (int i = 0; i < splitPosition; i++) {
            assertThat(iter.hasNext()).isTrue();
            assertThat(iter.next()).isEqualTo(DATA[i]);
        }
        assertThat(iter.hasNext()).isTrue();

        Stream<String> remaining = iter.getRemainingStream();

        AtomicInteger index = new AtomicInteger(splitPosition);
        remaining.forEach(datum -> {
            assertThat(datum).isEqualTo(DATA[index.getAndIncrement()]);
        });
    }

    @Test
    @DisplayName("disallows iteration after retrieving remaining Stream")
    public void testNoIteratorAfterRemainingStream() {
        CloseableStreamIterator<String> iter = newInstance();

        int splitPosition = 2;

        for (int i = 0; i < 2; i++) {
            assertThat(iter.hasNext()).isTrue();
            assertThat(iter.next()).isEqualTo(DATA[i]);
        }
        assertThat(iter.hasNext()).isTrue();

        Stream<String> remaining = iter.getRemainingStream();

        String next = remaining.findFirst().get();
        assertThat(next).isEqualTo(DATA[splitPosition]);

        assertThatThrownBy(() -> iter.next())
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Remaining stream was already extracted");
    }

    @Test
    @DisplayName("closes stream after closing iterator")
    public void testClosesStreamAfterFullIteration() {
        try (CloseableStreamIterator<String> iter = newInstance()) {
            for (int i = 0; i < DATA.length; i++) {
                iter.next();
            }
        }

        verify(resource).close();
        verify(toStream, never()).accept(any(Iterator.class));
    }

    @Test
    @DisplayName("does not close stream after retrieving remaining stream")
    public void testDoesNotCloseOriginalStreamImmediately() {
        doAnswer(no -> {
            new Exception().printStackTrace();
            return null;
        }).when(resource).close();

        Stream<String> remaining = null;
        try (CloseableStreamIterator<String> iter = newInstance()) {
            remaining = iter.getRemainingStream();
        }

        verify(resource, never()).close();
    }

    @Test
    @DisplayName("closes stream when closing remaining stream")
    public void testClosesOriginalStream() {

        List<String> firstElements = new ArrayList<>();
        try (Stream<String> stream = splitStream(firstElements, 2)) {
            assertThat(firstElements).containsExactly("A", "B");
            assertThat(stream).containsExactly("C", "D", "E");
            verify(resource, never()).close();
        }

        verify(resource).close();
    }

    private Stream<String> splitStream(List<String> collector, int splitIndex) {
        try (CloseableStreamIterator<String> iter = newInstance()) {
            for (int i = 0; i < splitIndex; i++) {
                collector.add(iter.next());
            }
            return iter.getRemainingStream();
        }
    }

    @Test
    @DisplayName("closes original Stream when iterating too far")
    public void testClosesOriginalStreamAfterOverIteration() {

        try (CloseableStreamIterator<String> iter = newInstance()) {
            for (int i = 0; i < DATA.length + 1; i++) {
                iter.next();
            }
            fail("Expected NoSuchElementException to be thrown");
        } catch (NoSuchElementException exc) {

        }

        verify(resource).close();
        verify(toStream, never()).accept(any(Iterator.class));
    }

    @Test
    @DisplayName("closes original Stream after Exception creating remaining Stream")
    public void testClosesOriginalStreamAfterExceptionCreatingRemainingStream() {
        doThrow(new UnsupportedOperationException("Test")).when(toStream).accept(any(Iterator.class));

        try (CloseableStreamIterator<String> iter = newInstance()) {
            for (int i = 0; i < 2; i++) {
                iter.next();
            }
            iter.getRemainingStream();
            fail("Expected UnsupportedOperationException to be thrown");
        } catch (UnsupportedOperationException exc) {

        }

        verify(toStream).accept(any(Iterator.class));
        verify(resource).close();
    }

    @Test
    @DisplayName("closes original Stream after Exception in constructor")
    public void testClosesOriginalStreamAfterExceptionInConstructor() {

        try {
            CloseableStreamIterator<String> iter = newInstance(actualStream -> {
                Stream<String> riggedStream = mockDelegateTo(Stream.class, actualStream);
                when(riggedStream.iterator()).thenThrow(new UnsupportedOperationException("Test"));
                return riggedStream;
            });
            fail("Expected UnsupportedOperationException to be thrown");
        } catch (UnsupportedOperationException exc) {

        }

        verify(resource).close();

    }

    @Test
    @DisplayName("closes stream after Exception when chaining")
    public void testClosesOriginalStreamAfterChainingException() {
        Stream<String> remaining;
        try (CloseableStreamIterator<String> iter = newInstance()) {
            remaining = iter.getRemainingStream();

            try {
                remaining = remaining.map(null);
            } catch (Exception exc) {
                try (Stream<String> toBeClosed = remaining) {
                    throw exc;
                }
            }

            fail("Expected NullPointerException to be thrown");
        } catch (NullPointerException exc) {

        }

        verify(resource).close();

    }

    private interface SilentCloseable extends AutoCloseable {
        public void close();
    }

    private class CloseableStreamIteratorForTest<T> extends CloseableStreamIterator<T> {

        public CloseableStreamIteratorForTest(Supplier<Stream<T>> streamSupplier) {
            super(streamSupplier);
        }

        @Override
        Stream<T> toStream(Iterator<T> iterator) {
            toStream.accept(iterator);
            return super.toStream(iterator);
        }
    }

    private <T> T mockDelegateTo(Class<T> type, T instance) {
        return Mockito.mock(type, Mockito.withSettings().defaultAnswer(delegateTo(instance)));
    }

    private Answer<?> delegateTo(Object object) {
        return (Answer<Object>) invocation ->
                invocation.getMethod().invoke(object, invocation.getArguments());
    }

}
