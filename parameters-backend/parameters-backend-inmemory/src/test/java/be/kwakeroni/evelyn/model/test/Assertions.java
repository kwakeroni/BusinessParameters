package be.kwakeroni.evelyn.model.test;

import org.assertj.core.api.ThrowableAssert;

public class Assertions extends org.assertj.core.api.Assertions {

    public static ParseExceptionAssert assertThatParseExceptionThrownBy(ThrowableAssert.ThrowingCallable shouldRaiseThrowable) {
        return ParseExceptionAssert.assertThatThrownBy(shouldRaiseThrowable);
    }

}
