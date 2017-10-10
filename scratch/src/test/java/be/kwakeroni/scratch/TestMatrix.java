package be.kwakeroni.scratch;

import be.kwakeroni.scratch.env.TestData;
import be.kwakeroni.scratch.env.es.ElasticSearchTestData;
import be.kwakeroni.scratch.env.inmemory.InMemoryTestData;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * Created by kwakeroni on 25/04/17.
 */
public class TestMatrix {

    private static final List<TestParameter<Supplier<TestData>>> BACKENDS = Arrays.asList(
            data("InMemory", InMemoryTestData::new),
            data("ElasticSearch", ElasticSearchTestData::new)
    );

    public static TestMatrix backends() {
        return new TestMatrix(BACKENDS::stream);
    }

    private Iterable<Supplier<Stream<?>>> supplier;

    private TestMatrix(Supplier<Stream<?>>... suppliers) {
        supplier = Arrays.asList(suppliers);
    }

    public Object[][] toParameterArray() {
        return zip(supplier.iterator()).toArray(Object[][]::new);
    }

    static TestParameter<Supplier<TestData>> data(String name, Supplier<TestData> supplier) {
        return named(name, supplier);
    }

    static <V> TestParameter<V> named(String name, V value) {
        return new TestParameter<>(name, value);
    }

    static class TestParameter<V> {
        final String name;
        final V value;

        public TestParameter(String name, V value) {
            this.name = name;
            this.value = value;
        }

        public String toString() {
            return this.name;
        }
    }

    private static Stream<Object[]> zip(Iterator<Supplier<Stream<?>>> data) {
        return zip(Tuple.empty(), data).map(Tuple::toArray);

    }

    private static Stream<Tuple> zip(Tuple prefix, Iterator<Supplier<Stream<?>>> data) {
        if (!data.hasNext()) {
            return Stream.of(prefix);
        }


        Stream<Tuple> stream =
                data.next().get().map(prefix::postfix);

        return (data.hasNext()) ? stream.flatMap(tuple -> zip(tuple, data)) : stream;
    }


    private static class Tuple {
        private final List<Object> list;

        private Tuple(List<Object> list) {
            this.list = Collections.unmodifiableList(list);
        }

        static Tuple empty() {
            return new Tuple(Collections.emptyList());
        }

        static Tuple of(Object... objects) {
            List<Object> list = new ArrayList<>(objects.length);
            list.addAll(Arrays.asList(objects));
            return new Tuple(list);
        }

        Tuple prefix(Object o) {
            List<Object> list = new ArrayList<>(this.list.size() + 1);
            list.add(o);
            list.addAll(this.list);
            return new Tuple(list);
        }

        Tuple postfix(Object o) {
            List<Object> list = new ArrayList(this.list.size() + 1);
            list.addAll(this.list);
            list.add(o);
            return new Tuple(list);
        }

        Object[] toArray() {
            return this.list.toArray();
        }
    }
}
