package be.kwakeroni.parameters.basic.client.model;

import java.util.Comparator;
import java.util.function.Function;

/**
 * (C) 2016 Maarten Van Puymbroeck
 */
public interface Range<T> {

    public T getFrom();

    public T getTo();

    public boolean contains(T value);

    public static <T> Range<T> of(T from, T to, Comparator<? super T> comparator){
        return new DefaultRange<>(from, to, comparator);
    }

    public static <T extends Comparable<? super T>> Range<T> of(T from, T to){
        return of(from, to, Comparator.naturalOrder());
    }
}
