package be.kwakeroni.evelyn.model.impl;

import java.util.Iterator;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.Supplier;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

class CloseableStreamIterator<T> implements AutoCloseable, Iterator<T> {

    private Stream<T> stream;
    private Iterator<T> iterator;

    public CloseableStreamIterator(Supplier<Stream<T>> streamSupplier) {
        this.stream = streamSupplier.get();
        try {
            this.iterator = stream.iterator();
        } catch (RuntimeException exc) {
            // Ensure the stream is closed in case of an Exception
            try (Stream<T> s = this.stream) {
                throw exc;
            }
        }
    }

    private void assertOpen() {
        if (this.stream == null | this.iterator == null) {
            throw new IllegalStateException("Remaining stream was already extracted");
        }
    }

    @Override
    public boolean hasNext() {
        assertOpen();
        return iterator.hasNext();
    }

    @Override
    public T next() {
        assertOpen();
        return iterator.next();
    }

    @Override
    public void close() {
        if (this.stream != null) {
            this.stream.close();
        }
    }

    public Stream<T> getRemainingStream() {
        // Ensure 'this' is closed if we throw an Exception
        // Closing 'this' after returning normally will have no effect
        try (CloseableStreamIterator<T> self = this) {
            Stream<T> result = toStream(this.iterator)
                    .onClose(this.stream::close);

            this.stream = null;
            this.iterator = null;

            return result;
        }
    }

    Stream<T> toStream(Iterator<T> iterator) {
        return StreamSupport.stream(
                Spliterators.spliteratorUnknownSize(iterator, Spliterator.ORDERED), false);
    }
}
