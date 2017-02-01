package be.kwakeroni.parameters.basic.client.support;

import be.kwakeroni.parameters.client.api.model.Entry;
import be.kwakeroni.parameters.client.api.model.Parameter;

/**
 * (C) 2017 Maarten Van Puymbroeck
 */
public class Entries {

    private Entries() {

    }

    public static <T> Builder with(Parameter<T> parameter, T value) {
        return new DefaultEntryBuilder(5).with(parameter, value);
    }

    public static <A> Entry entryOf(Parameter<A> parameter, A value) {
        DefaultEntryBuilder builder = new DefaultEntryBuilder(1);
        builder.put(parameter, value);
        return builder.toEntry();
    }

    public static <A, B> Entry entryOf(Parameter<A> parameterA, A valueA, Parameter<B> parameterB, B valueB) {
        DefaultEntryBuilder builder = new DefaultEntryBuilder(2);
        builder.put(parameterA, valueA);
        builder.put(parameterB, valueB);
        return builder.toEntry();
    }

    public static <A, B, C> Entry entryOf(
            Parameter<A> parameterA, A valueA,
            Parameter<B> parameterB, B valueB,
            Parameter<C> parameterC, C valueC) {
        DefaultEntryBuilder builder = new DefaultEntryBuilder(2);
        builder.put(parameterA, valueA);
        builder.put(parameterB, valueB);
        builder.put(parameterC, valueC);
        return builder.toEntry();
    }

    public static interface Builder {
        <T> Builder with(Parameter<T> parameter, T value);

        Entry toEntry();
    }
}
