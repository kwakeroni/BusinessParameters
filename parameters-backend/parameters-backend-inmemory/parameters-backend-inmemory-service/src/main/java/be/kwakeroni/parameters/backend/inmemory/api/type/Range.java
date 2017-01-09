package be.kwakeroni.parameters.backend.inmemory.api.type;

import java.util.function.Function;

/**
 * (C) 2016 Maarten Van Puymbroeck
 */
public interface Range<T> {

    T getFrom();

    T getTo();

    boolean contains(T value);

}
