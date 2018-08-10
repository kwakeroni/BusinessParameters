package be.kwakeroni.parameters.core.support.util.function;

import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Represents a predicate (boolean-valued function) of one argument that can throw an exception.
 *
 * <p>This is a <a href="package-summary.html">functional interface</a>
 * whose functional method is {@link #test(T)}.
 *
 * @param <T> the type of the input to the predicate
 * @param <E> the type of the exception that can be thrown by the function
 * @see Predicate
 */
@SuppressWarnings("unused")
public interface ThrowingPredicate<T, E extends Exception> {

    /**
     * Evaluates this predicate on the given argument.
     *
     * @param t the input argument
     * @return {@code true} if the input argument matches the predicate,
     * otherwise {@code false}
     * @throws E an Exception
     */
    public boolean test(T t) throws E;

    /**
     * Returns a composed predicate that represents a short-circuiting logical
     * AND of this predicate and another.  When evaluating the composed
     * predicate, if this predicate is {@code false}, then the {@code other}
     * predicate is not evaluated.
     *
     * <p>Any exceptions thrown during evaluation of either predicate are relayed
     * to the caller; if evaluation of this predicate throws an exception, the
     * {@code other} predicate will not be evaluated.
     *
     * @param other a predicate that will be logically-ANDed with this
     *              predicate
     * @return a composed predicate that represents the short-circuiting logical
     * AND of this predicate and the {@code other} predicate
     * @throws NullPointerException if other is null
     */
    default ThrowingPredicate<T, E> and(ThrowingPredicate<? super T, ? extends E> other) {
        Objects.requireNonNull(other);
        return (t) -> test(t) && other.test(t);
    }

    /**
     * Returns a predicate that represents the logical negation of this
     * predicate.
     *
     * @return a predicate that represents the logical negation of this
     * predicate
     */
    default ThrowingPredicate<T, E> negate() {
        return (t) -> !test(t);
    }

    /**
     * Returns a composed predicate that represents a short-circuiting logical
     * OR of this predicate and another.  When evaluating the composed
     * predicate, if this predicate is {@code true}, then the {@code other}
     * predicate is not evaluated.
     *
     * <p>Any exceptions thrown during evaluation of either predicate are relayed
     * to the caller; if evaluation of this predicate throws an exception, the
     * {@code other} predicate will not be evaluated.
     *
     * @param other a predicate that will be logically-ORed with this
     *              predicate
     * @return a composed predicate that represents the short-circuiting logical
     * OR of this predicate and the {@code other} predicate
     * @throws NullPointerException if other is null
     */
    default ThrowingPredicate<T, E> or(ThrowingPredicate<? super T, ? extends E> other) {
        Objects.requireNonNull(other);
        return (t) -> test(t) || other.test(t);
    }

    /**
     * Returns a predicate that represents the logical negation of the given predicate.
     *
     * @param throwingPredicate The predicate to be negated
     * @param <T>               the type of the input to the predicate
     * @param <E>               the type of the exception that can be thrown by the function
     * @return a predicate that represents the logical negation of {@code throwingPredicate}
     */
    public static <T, E extends Exception> ThrowingPredicate<T, E> not(ThrowingPredicate<T, E> throwingPredicate) {
        return throwingPredicate.negate();
    }

    /**
     * Returns a predicate that tests the given <em>throwing</em> predicate
     * and wraps any thrown exception into a {@link RuntimeException}.
     *
     * @param throwingPredicate the predicate to delegate to
     * @param <T>               the type of the input to the predicate
     * @return a predicate that tests {@code throwingPredicate} and wraps any exception into a {@link RuntimeException}
     */
    public static <T> Predicate<T> unchecked(ThrowingPredicate<T, ?> throwingPredicate) {
        return unchecked(throwingPredicate, RuntimeException::new);
    }

    /**
     * Returns a predicate that tests the given <em>throwing</em> predicate
     * and wraps any thrown exception into a {@link RuntimeException}.
     *
     * @param throwingPredicate the predicate to delegate to
     * @param exceptionWrapper  The function to wrap a thrown exception into a runtime exception
     * @param <T>               the type of the input to the predicate
     * @return a predicate that tests {@code throwingPredicate} and wraps any exception into a {@link RuntimeException} produced by {@code exceptionWrapper}
     */
    public static <T> Predicate<T> unchecked(ThrowingPredicate<T, ?> throwingPredicate, Function<? super Exception, ? extends RuntimeException> exceptionWrapper) {
        return t -> {
            try {
                return throwingPredicate.test(t);
            } catch (Exception exc) {
                throw exceptionWrapper.apply(exc);
            }
        };
    }

}
