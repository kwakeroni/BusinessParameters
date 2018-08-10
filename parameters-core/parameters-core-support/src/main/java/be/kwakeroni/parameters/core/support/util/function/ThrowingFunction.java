package be.kwakeroni.parameters.core.support.util.function;

import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Represents a function that accepts one argument and produces a result or throws an exception.
 *
 * <p>This is a <a href="package-summary.html">functional interface</a>
 * whose functional method is {@link #apply(T)}.
 *
 * @param <T> the type of the input to the function
 * @param <R> the type of the result of the function
 * @param <E> the type of the exception that can be thrown by the function
 * @see Function
 */
@SuppressWarnings("unused")
public interface ThrowingFunction<T, R, E extends Exception> {

    /**
     * Applies this function to the given argument.
     *
     * @param t the function argument
     * @return the function result
     * @throws E an Exception
     */
    public R apply(T t) throws E;


    /**
     * Returns a composed function that first applies the {@code before}
     * function to its input, and then applies this function to the result.
     * If evaluation of either function throws an exception, it is relayed to
     * the caller of the composed function.
     *
     * @param <V>    the type of input to the {@code before} function, and to the
     *               composed function
     * @param before the function to apply before this function is applied
     * @return a composed function that first applies the {@code before}
     * function and then applies this function
     * @throws NullPointerException if before is null
     * @see #andThen(ThrowingFunction)
     */
    default <V> ThrowingFunction<V, R, E> compose(ThrowingFunction<? super V, ? extends T, ? extends E> before) {
        Objects.requireNonNull(before);
        return (V v) -> apply(before.apply(v));
    }

    /**
     * Returns a composed function that first applies this function to
     * its input, and then applies the {@code after} function to the result.
     * If evaluation of either function throws an exception, it is relayed to
     * the caller of the composed function.
     *
     * @param <V>   the type of output of the {@code after} function, and of the
     *              composed function
     * @param after the function to apply after this function is applied
     * @return a composed function that first applies this function and then
     * applies the {@code after} function
     * @throws NullPointerException if after is null
     * @see #compose(ThrowingFunction)
     */
    default <V> ThrowingFunction<T, V, E> andThen(ThrowingFunction<? super R, ? extends V, ? extends E> after) {
        Objects.requireNonNull(after);
        return (T t) -> after.apply(apply(t));
    }

    /**
     * Returns a function that always returns its input argument.
     *
     * @param <T> the type of the input and output objects to the function
     * @return a function that always returns its input argument
     */
    public static <T, E extends Exception> ThrowingFunction<T, T, E> identity() {
        return t -> t;
    }

    /**
     * Returns a function that always throws an exception.
     *
     * @param exceptionSupplier Supplier of the exception to be thrown
     * @param <T>               the type of the input to the function
     * @param <R>               the type of the result of the function
     * @param <E>               the type of the exception to be thrown by the function
     * @return a function that always throws an exception fetched from the provided {@code exceptionSupplier}
     */
    public static <T, R, E extends Exception> ThrowingFunction<T, R, E> throwing(Supplier<? extends E> exceptionSupplier) {
        return t -> {
            throw exceptionSupplier.get();
        };
    }

    /**
     * Returns a function that returns the result of the given <em>throwing</em> function
     * or wraps any thrown exception into a {@link RuntimeException}.
     *
     * @param throwingFunction The function to delegate to
     * @param <T>              the type of the input to the function
     * @param <R>              the type of the result of the function
     * @return a function that delegates to {@code throwingFunction} and wraps any exception into a RuntimeException.
     */
    public static <T, R> Function<T, R> unchecked(ThrowingFunction<T, R, ?> throwingFunction) {
        return unchecked(throwingFunction, RuntimeException::new);
    }

    /**
     * Returns a function that returns the result of the given <em>throwing</em> function
     * or wraps any thrown exception into a {@link RuntimeException}.
     *
     * @param throwingFunction The function to delegate to
     * @param exceptionWrapper The function to wrap a thrown exception into a runtime exception
     * @param <T>              the type of the input to the function
     * @param <R>              the type of the result of the function
     * @return a function that delegates to {@code throwingFunction} and wraps any exception into a RuntimeException produced by {@code exceptionWrapper}.
     */
    public static <T, R> Function<T, R> unchecked(ThrowingFunction<T, R, ?> throwingFunction, Function<? super Exception, ? extends RuntimeException> exceptionWrapper) {
        return t -> {
            try {
                return throwingFunction.apply(t);
            } catch (Exception exc) {
                throw exceptionWrapper.apply(exc);
            }
        };
    }

}
