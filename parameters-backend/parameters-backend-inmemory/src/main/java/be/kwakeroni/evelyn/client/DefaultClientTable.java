package be.kwakeroni.evelyn.client;

import be.kwakeroni.evelyn.model.DatabaseAccessor;
import be.kwakeroni.evelyn.model.Event;
import be.kwakeroni.evelyn.model.ParseException;
import be.kwakeroni.evelyn.model.RuntimeParseException;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Stream;

import static java.util.stream.Collectors.groupingBy;

public final class DefaultClientTable<E> implements ClientTable<E> {

    private final DatabaseAccessor accessor;
    private final Function<String, ? extends ClientOperation<E>> operationMap;
    private final Clock clock;

    public DefaultClientTable(DatabaseAccessor accessor, Function<String, ? extends ClientOperation<E>> operationMap) {
        this(accessor, operationMap, Clock.systemDefaultZone());
    }

    DefaultClientTable(DatabaseAccessor accessor, Function<String, ? extends ClientOperation<E>> operationMap, Clock clock) {
        this.accessor = Objects.requireNonNull(accessor, "accessor");
        this.operationMap = Objects.requireNonNull(operationMap, "operationMap");
        this.clock = clock;
    }

    @Override
    public String getName() {
        return accessor.getDatabaseName();
    }

    @Override
    public void append(String user, String operation, String objectId, String data) {
        Event event = new DefaultEvent(user, operation, objectId, LocalDateTime.now(clock), data);
        this.accessor.append(event);
    }

    @Override
    public Stream<E> findAll() {
        Stream<Event> events;
        try {
            events = accessor.getData();
        } catch (ParseException e) {
            throw new RuntimeParseException(e);
        }
        try {
            return events
                    .collect(groupingBy(Event::getObjectId, reducingEvents(this::applyEvent)))
                    .values()
                    .stream();
        } catch (RuntimeException exc) {
            try (Stream<Event> resource = events) {
                throw exc;
            }
        }
    }

    public E findById(String id) {
        Objects.requireNonNull(id, "id");

        try (Stream<Event> events = accessor.getData()) {
            return events
                    .filter(event -> id.equals(event.getObjectId()))
                    .reduce(null, this::applyEvent, notInParallel());
        } catch (ParseException e) {
            throw new RuntimeParseException(e);
        }

    }

    private E applyEvent(E e, Event event) {
        return operationMap.apply(event.getOperation()).operate(e, event);
    }

    private static <T> BinaryOperator<T> notInParallel() {
        return (e1, e2) -> {
            throw new UnsupportedOperationException("Parallel mode not supported");
        };
    }

    private static <E> Collector<Event, ?, E> reducingEvents(BiFunction<E, Event, E> operator) {
        return Collector.of(
                () -> box((E) null),
                (entity, event) -> entity[0] = operator.apply(entity[0], event),
                notInParallel(),
                entity -> entity[0]
        );
    }

    @SafeVarargs
    private static <T> T[] box(T... singleton) {
        return singleton;
    }
}
