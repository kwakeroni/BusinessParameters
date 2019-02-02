package be.kwakeroni.scratch;

import be.kwakeroni.parameters.client.api.model.Entry;
import be.kwakeroni.parameters.client.api.model.Parameter;
import org.assertj.core.api.AbstractAssert;
import org.assertj.core.description.Description;
import org.assertj.core.internal.ObjectArrays;

import java.util.Arrays;
import java.util.function.Function;

public interface EntryAssert {

    public static EntryAssert assertThat(Entry entry) {
        return new Impl(entry);
    }

    ValuesAssert hasParameters(Parameter<?>... parameters);


    public EntryAssert as(String description, Object... args);

    public EntryAssert as(Description description);

    public EntryAssert describedAs(String description, Object... args);

    public EntryAssert describedAs(Description description);

    public void isNull();

    public EntryAssert isNotNull();

    public interface ValuesAssert {
        EntryAssert withValues(Object... values);
    }

    public class Impl extends AbstractAssert<Impl, Entry> implements EntryAssert {
        public Impl(Entry actual) {
            super(actual, Impl.class);
        }

        @Override
        public ValuesAssert hasParameters(Parameter<?>... parameters) {
            String[] actualParams = actual.toMap().keySet().toArray(new String[0]);
            ObjectArrays.instance().assertContainsExactlyInAnyOrder(info, actualParams, map(parameters, Parameter::getName));
            return new Values(parameters);
        }

        @Override
        public Impl as(String description, Object... args) {
            return super.as(description, args);
        }

        @Override
        public Impl as(Description description) {
            return super.as(description);
        }

        @Override
        public Impl describedAs(String description, Object... args) {
            return super.describedAs(description, args);
        }

        @Override
        public Impl describedAs(Description description) {
            return super.describedAs(description);
        }

        @Override
        public void isNull() {
            super.isNull();
        }

        @Override
        public Impl isNotNull() {
            return super.isNotNull();
        }

        private static <T, S> S[] map(T[] array, Function<T, S> mapper) {
            return (S[]) Arrays.stream(array).map(mapper).toArray();
        }

        private class Values implements ValuesAssert {
            private final Parameter<?>[] parameters;

            public Values(Parameter<?>[] parameters) {
                this.parameters = parameters;
            }

            @Override
            public EntryAssert withValues(Object... values) {
                ObjectArrays.instance().assertContainsExactly(info, map(parameters, actual::getValue), values);
                return Impl.this;
            }
        }

    }


}
