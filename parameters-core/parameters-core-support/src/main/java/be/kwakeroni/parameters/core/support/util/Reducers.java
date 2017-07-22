package be.kwakeroni.parameters.core.support.util;

import java.util.function.BinaryOperator;

/**
 * Created by kwakeroni on 29/06/17.
 */
public class Reducers {

    public static <T> BinaryOperator<T> atMostOne() {
        return atMostOne("At most one instance expected. Multiple instances found: %s and %s");
    }

    public static <T> BinaryOperator<T> atMostOne(String messagePattern) {
        return (t1, t2) -> {
            throw new IllegalStateException(String.format(messagePattern, t1, t2));
        };
    }

}
