package be.kwakeroni.parameters.basic.type;

import java.util.Comparator;
import java.util.Objects;

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
    public boolean overlaps(Range<V> otherRange) {
        V otherFrom = otherRange.getFrom();
        V otherTo = otherRange.getTo();
        return (comparator.compare(otherTo, this.from) > 0 && comparator.compare(otherTo, this.to) < 0)
                || (comparator.compare(otherFrom, this.from) >= 0 && comparator.compare(otherFrom, this.to) < 0)
                || (comparator.compare(this.from, otherFrom) >= 0 && comparator.compare(this.from, otherTo) < 0)
                ;//|| (comparator.compare(this.to, otherFrom) > 0 && comparator.compare(this.to, otherTo) < 0);

//          otherTo in ]this.from, this.to[
//          || otherFrom in [this.from, this.to[
//          || this.from in [otherFrom, otherTo[
//          || this.to in ]otherFrom, otherTo[

//        otherTo > this.from && otherTo < this.to
//        || otherFrom >= this.from && otherFrom < this.to
//        || this.from >= otherFrom && this.from < otherTo
//        || this.to > otherFrom && this.to < otherTo
//

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
