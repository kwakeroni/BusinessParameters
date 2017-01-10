package be.kwakeroni.parameters.basic.client.model;

import java.util.Comparator;
import java.util.Objects;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * (C) 2016 Maarten Van Puymbroeck
 */
class DefaultRange<V> implements Range<V> {

    private final Comparator<? super V> comparator;
    private final V from;
    private final V to;


    DefaultRange(V from, V toExclusive, Comparator<? super V> comparator) {
        this.comparator = comparator;
        this.from = from;
        this.to = toExclusive;
    }

    @Override
    public V getFrom() {
        return from;
    }

    @Override
    public V getTo() {
        return to;
    }

    public boolean contains(V value) {
        return comparator.compare(value, this.to) < 0 && comparator.compare(value, this.from) >= 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DefaultRange<?> that = (DefaultRange<?>) o;
        return Objects.equals(comparator, that.comparator) &&
                Objects.equals(from, that.from) &&
                Objects.equals(to, that.to);
    }

    @Override
    public int hashCode() {
        return Objects.hash(comparator, from, to);
    }

    @Override
    public String toString() {
        return "[" + from + "," + to + "]";
    }
}
