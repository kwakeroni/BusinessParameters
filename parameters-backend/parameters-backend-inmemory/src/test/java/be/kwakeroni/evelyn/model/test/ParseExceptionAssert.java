package be.kwakeroni.evelyn.model.test;

import be.kwakeroni.evelyn.model.ParseException;
import org.assertj.core.api.AbstractThrowableAssert;
import org.assertj.core.api.ThrowableAssert;
import org.assertj.core.internal.Failures;

import java.util.Optional;
import java.util.OptionalInt;

import static org.assertj.core.api.Assertions.catchThrowable;

public class ParseExceptionAssert extends AbstractThrowableAssert<ParseExceptionAssert, Throwable> {

    public ParseExceptionAssert(Throwable actual) {
        super(actual, ParseExceptionAssert.class);
    }

    public static ParseExceptionAssert assertThatThrownBy(ThrowableAssert.ThrowingCallable shouldRaiseThrowable) {
        return new ParseExceptionAssert(catchThrowable(shouldRaiseThrowable))
                .hasBeenThrown()
                .isInstanceOf(ParseException.class);
    }

    private ParseException actual() {
        return (ParseException) this.actual;
    }

    public ParseExceptionAssert hasLine(int expected) {
        if (!hasValue(actual().getLine(), expected)) {
            throw Failures.instance().failure("Expected line number: " + expected + " in " + this.actual);
        }
        return this;
    }

    public ParseExceptionAssert hasPosition(int expected) {
        if (!hasValue(actual().getPosition(), expected)) {
            throw Failures.instance().failure("Expected position: " + expected + " in " + this.actual);
        }
        return this;
    }

    public ParseExceptionAssert hasSource(String expected) {
        if (!hasValue(actual().getSource(), expected)) {
            throw Failures.instance().failure("Expected source: " + expected + " in " + this.actual);
        }
        return this;
    }

    private boolean hasValue(OptionalInt optional, int value) {
        return optional.isPresent() && optional.getAsInt() == value;
    }

    private <T> boolean hasValue(Optional<T> optional, T value) {
        return optional.isPresent() && value.equals(optional.get());
    }

}
